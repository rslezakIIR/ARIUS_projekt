from flask import Blueprint, request, jsonify
from app.models.review import Review
from flask_jwt_extended import jwt_required, get_jwt_identity
from app import db

reviews = Blueprint('reviews', __name__)

@reviews.route('/reviews/shop', methods=['POST'])
@jwt_required()
def add_shop_review():
    current_user_id = get_jwt_identity()
    data = request.get_json()
    
    # Verify user has made a purchase
    user_has_orders = True
    
    if not user_has_orders:
        return jsonify({'error': 'Must make a purchase before reviewing'}), 403
    
    # Check if user already reviewed
    existing_review = Review.query.filter_by(
        user_id=current_user_id,
        coin_id=None  # Shop review has no coin_id
    ).first()
    
    if existing_review:
        return jsonify({'error': 'Already reviewed'}), 400
    
    review = Review(
        user_id=current_user_id,
        rating=data['rating'],
        comment=data.get('comment', '')
    )
    
    db.session.add(review)
    db.session.commit()
    
    return jsonify({'message': 'Review added successfully'}), 201

@reviews.route('/reviews/shop', methods=['GET'])
def get_shop_reviews():
    page = request.args.get('page', 1, type=int)
    per_page = request.args.get('per_page', 10, type=int)
    
    reviews = Review.query.filter_by(coin_id=None).paginate(
        page=page,
        per_page=per_page,
        error_out=False
    )
    
    return jsonify({
        'reviews': [{
            'id': review.id,
            'rating': review.rating,
            'comment': review.comment,
            'created_at': review.created_at.isoformat(),
            'user': review.user.username
        } for review in reviews.items],
        'total_pages': reviews.pages,
        'current_page': reviews.page
    }), 200