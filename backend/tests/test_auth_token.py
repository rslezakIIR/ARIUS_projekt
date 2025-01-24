import pytest
from flask_jwt_extended import create_access_token, decode_token
from app import create_app

@pytest.fixture
def app_context():
    app = create_app()
    app.config['TESTING'] = True
    app.config['JWT_SECRET_KEY'] = "test_secret_key"
    with app.app_context():
        yield app

def test_token_generation_and_decoding(app_context):
    # Tworzymy token dostępu
    user_id = "1"  # Przykładowy ID użytkownika jako ciąg znaków
    access_token = create_access_token(identity=user_id)

    # Sprawdzamy, czy token nie jest pusty
    assert access_token is not None

    # Dekodujemy token i sprawdzamy jego zawartość
    decoded_token = decode_token(access_token)

    # Sprawdzamy, czy token zawiera poprawny identyfikator użytkownika
    assert decoded_token["sub"] == user_id
    assert "exp" in decoded_token  # Sprawdzamy, czy token ma datę wygaśnięcia
