from flask import Blueprint, request, jsonify
from app.models.user import User
from app import db
from flask_jwt_extended import (
    create_access_token,
    create_refresh_token,
    jwt_required,
    get_jwt_identity,
    unset_jwt_cookies,
)
import logging

logging.basicConfig(level=logging.DEBUG)

auth = Blueprint('auth', __name__)

@auth.route('/register', methods=['POST'])
def register():
    """Rejestracja nowego użytkownika."""
    data = request.get_json()
    logging.debug(f"Received registration data: {data}")

    if not data.get('email') or not data.get('name') or not data.get('password'):
        return jsonify({'error': 'Wszystkie pola są wymagane'}), 400

    if User.query.filter_by(email=data['email']).first():
        return jsonify({'error': 'Email już istnieje'}), 400

    if User.query.filter_by(username=data['name']).first():
        return jsonify({'error': 'Nazwa użytkownika zajęta'}), 400

    user = User(
        email=data['email'],
        username=data['name'],  # Użyj 'name' jako 'username'
    )
    user.set_password(data['password'])

    db.session.add(user)
    db.session.commit()

    logging.info(f"User created: {user.email}")
    return jsonify({'message': 'Rejestracja zakończona sukcesem'}), 201



@auth.route('/login', methods=['POST'])
def login():
    """Logowanie użytkownika."""
    try:
        data = request.get_json()
        logging.debug(f"Login attempt with data: {data}")

        if not data or not data.get('email') or not data.get('password'):
            logging.error("Missing email or password in request")
            return jsonify({'error': 'Email i hasło są wymagane'}), 400

        user = User.query.filter_by(email=data['email']).first()
        logging.debug(f"User found: {user}")

        if user and user.check_password(data['password']):
            access_token = create_access_token(identity=user.id)
            refresh_token = create_refresh_token(identity=user.id)
            logging.info("Login successful")
            return jsonify({
                'access_token': access_token,
                'refresh_token': refresh_token,
                'user': {
                    'id': user.id,
                    'username': user.username,
                    'email': user.email,
                }
            }), 200

        logging.warning("Invalid login credentials")
        return jsonify({'error': 'Nieprawidłowe dane logowania'}), 401

    except Exception as e:
        logging.exception("Unexpected error during login")
        return jsonify({'error': 'Nieznany błąd'}), 500

@auth.route('/refresh', methods=['POST'])
@jwt_required(refresh=True)
def refresh_token():
    """Odświeżanie tokenu JWT."""
    current_user = get_jwt_identity()
    print(f"Refreshing token for user ID: {current_user}")
    new_access_token = create_access_token(identity=current_user)
    return jsonify({'access_token': new_access_token}), 200


@auth.route('/logout', methods=['POST'])
def logout():
    """Wylogowanie użytkownika."""
    response = jsonify({'message': 'Wylogowanie zakończone sukcesem'})
    unset_jwt_cookies(response)
    print("User logged out successfully.")  # Logowanie wylogowania
    return response, 200


@auth.route('/profile', methods=['GET'])
@jwt_required()
def profile():
    """Pobieranie profilu użytkownika."""
    current_user_id = get_jwt_identity()
    print(f"Fetching profile for user ID: {current_user_id}")

    user = User.query.get(current_user_id)
    if not user:
        print(f"User with ID {current_user_id} not found.")
        return jsonify({'error': 'Użytkownik nie istnieje'}), 404

    return jsonify({
        'id': user.id,
        'email': user.email,
        'username': user.username,
        'created_at': user.created_at.isoformat(),
    }), 200
