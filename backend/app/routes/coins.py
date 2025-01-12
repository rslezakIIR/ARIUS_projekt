from flask import Blueprint, request, jsonify
from app.models.coin import Coin
from app.models.review import Review
from flask_jwt_extended import jwt_required, get_jwt_identity
from app import db

coins = Blueprint('coins', __name__)

# Get a paginated list of coins
@coins.route('/', methods=['GET'])
def get_coins():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    
    # Paginate the query
    coins = Coin.query.paginate(page=page, per_page=per_page, error_out=False)
    
    return jsonify({
        'coins': [{
            'id': coin.id,
            'name': coin.name,
            'description': coin.description,
            'price': coin.price,
            'year_issued': coin.year_issued,
            'country': coin.country,
            'quantity_available': coin.quantity_available,
            'image_url': coin.image_url
        } for coin in coins.items],
        'total_pages': coins.pages,
        'current_page': coins.page
    }), 200

# Get details for a specific coin
@coins.route('/<int:coin_id>', methods=['GET'])
def get_coin(coin_id):
    coin = Coin.query.get_or_404(coin_id)
    reviews = Review.query.filter_by(coin_id=coin_id).all()
    
    # Calculate average rating
    avg_rating = 0
    if reviews:
        avg_rating = sum(review.rating for review in reviews) / len(reviews)
    
    return jsonify({
        'id': coin.id,
        'name': coin.name,
        'description': coin.description,
        'price': coin.price,
        'year_issued': coin.year_issued,
        'country': coin.country,
        'quantity_available': coin.quantity_available,
        'image_url': coin.image_url,
        'average_rating': round(avg_rating, 1),
        'reviews_count': len(reviews)
    }), 200

# Add a review for a coin
@coins.route('/<int:coin_id>/reviews', methods=['POST'])
@jwt_required()
def add_review(coin_id):
    current_user_id = get_jwt_identity()
    data = request.get_json()
    
    # Check if user has purchased this coin
    # Example: Add your own logic based on your order model
    # purchased = Order.query.filter_by(user_id=current_user_id, coin_id=coin_id).first()
    # if not purchased:
    #     return jsonify({'message': 'You must purchase this coin before reviewing it'}), 403
    
    # Add review
    review = Review(
        user_id=current_user_id,
        coin_id=coin_id,
        rating=data['rating'],
        comment=data.get('comment', '')
    )
    
    db.session.add(review)
    db.session.commit()
    
    return jsonify({'message': 'Review added successfully'}), 201

# Search for coins by query, country, and price range
@coins.route('/search', methods=['GET'])
def search_coins():
    query = request.args.get('q', '')
    country = request.args.get('country', '')
    min_price = request.args.get('min_price', type=float)
    max_price = request.args.get('max_price', type=float)
    
    coin_query = Coin.query
    
    # Filter by name or description
    if query:
        coin_query = coin_query.filter(
            (Coin.name.ilike(f'%{query}%')) | 
            (Coin.description.ilike(f'%{query}%'))
        )
    
    # Filter by country
    if country:
        coin_query = coin_query.filter(Coin.country == country)
        
    # Filter by price range
    if min_price is not None:
        coin_query = coin_query.filter(Coin.price >= min_price)
    if max_price is not None:
        coin_query = coin_query.filter(Coin.price <= max_price)
    
    coins = coin_query.all()
    
    return jsonify({
        'coins': [{
            'id': coin.id,
            'name': coin.name,
            'description': coin.description,
            'price': coin.price,
            'country': coin.country,
            'image_url': coin.image_url
        } for coin in coins]
    }), 200
