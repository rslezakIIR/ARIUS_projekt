# app/routes/orders.py
from flask import Blueprint, request, jsonify
from app.models.order import Order, OrderItem
from app.models.coin import Coin
# from app.services.email_service import send_order_confirmation
from flask_jwt_extended import jwt_required, get_jwt_identity
from app import db

orders = Blueprint('orders', __name__)

@orders.route('/cart/add', methods=['POST'])
@jwt_required()
def add_to_cart():
    current_user_id = get_jwt_identity()
    data = request.get_json()
    
    coin = Coin.query.get_or_404(data['coin_id'])
    
    if coin.quantity_available < data['quantity']:
        return jsonify({'error': 'Not enough coins in stock'}), 400
    
    # Find or create pending order (cart)
    cart = Order.query.filter_by(
        user_id=current_user_id,
        status='pending'
    ).first()
    
    if not cart:
        cart = Order(user_id=current_user_id, status='pending')
        db.session.add(cart)
        db.session.commit()
    
    # Check if item already in cart
    cart_item = OrderItem.query.filter_by(
        order_id=cart.id,
        coin_id=coin.id
    ).first()
    
    if cart_item:
        cart_item.quantity += data['quantity']
    else:
        cart_item = OrderItem(
            order_id=cart.id,
            coin_id=coin.id,
            quantity=data['quantity'],
            price_at_time=coin.price
        )
        db.session.add(cart_item)
    
    db.session.commit()
    
    return jsonify({'message': 'Item added to cart'}), 200

@orders.route('/cart/remove/<int:item_id>', methods=['DELETE'])
@jwt_required()
def remove_from_cart(item_id):
    current_user_id = get_jwt_identity()
    cart_item = OrderItem.query.get_or_404(item_id)
    
    # Verify the item belongs to the user's cart
    if cart_item.order.user_id != current_user_id:
        return jsonify({'error': 'Unauthorized'}), 403
    
    db.session.delete(cart_item)
    db.session.commit()
    
    return jsonify({'message': 'Item removed from cart'}), 200

@orders.route('/cart', methods=['GET'])
@jwt_required()
def get_cart():
    current_user_id = get_jwt_identity()
    
    cart = Order.query.filter_by(
        user_id=current_user_id,
        status='pending'
    ).first()
    
    if not cart:
        return jsonify({'items': [], 'total': 0}), 200
    
    items = [{
        'id': item.id,
        'coin_id': item.coin_id,
        'coin_name': item.coin.name,
        'quantity': item.quantity,
        'price': item.price_at_time,
        'subtotal': item.quantity * item.price_at_time
    } for item in cart.items]
    
    total = sum(item['subtotal'] for item in items)
    
    return jsonify({
        'items': items,
        'total': total
    }), 200

# @orders.route('/orders/checkout', methods=['POST'])
# @jwt_required()
# def checkout():
#     current_user_id = get_jwt_identity()
#     cart = Order.query.filter_by(
#         user_id=current_user_id,
#         status='pending'
#     ).first_or_404()
    
#     # Here you would integrate with payment service
#     # For now, we'll assume payment is successful
    
#     # Update order status
#     cart.status = 'paid'
    
#     # Update coin quantities
#     for item in cart.items:
#         coin = item.coin
#         coin.quantity_available -= item.quantity
    
#     db.session.commit()
    
#     # Send confirmation email
#     send_order_confirmation(cart)
    
#     return jsonify({'message': 'Order placed successfully'}), 200

@orders.route('/orders', methods=['GET'])
@jwt_required()
def get_orders():
    current_user_id = get_jwt_identity()
    orders = Order.query.filter_by(
        user_id=current_user_id
    ).filter(Order.status != 'pending').all()
    
    return jsonify({
        'orders': [{
            'id': order.id,
            'status': order.status,
            'created_at': order.created_at.isoformat(),
            'total_amount': sum(item.quantity * item.price_at_time for item in order.items),
            'items': [{
                'coin_name': item.coin.name,
                'quantity': item.quantity,
                'price': item.price_at_time
            } for item in order.items]
        } for order in orders]
    }), 200