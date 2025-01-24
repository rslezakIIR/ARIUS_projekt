import pytest
from app import create_app

@pytest.fixture
def client():
    app = create_app()
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_auth_login_endpoint_accessibility(client):
    # Wysyłamy żądanie POST do /api/auth/login z przykładowymi danymi logowania
    response = client.post('/api/auth/login', json={
        "email": "john.doe@example.com",  # Przykładowy email
        "password": "password123"  # Przykładowe hasło
    })

    # Sprawdzamy, czy kod odpowiedzi to 200 OK
    assert response.status_code == 200

    # Pobieramy dane JSON z odpowiedzi
    data = response.get_json()

    # Sprawdzamy, czy odpowiedź zawiera tokeny
    assert "access_token" in data
    assert "refresh_token" in data

    # Sprawdzamy, czy odpowiedź zawiera poprawne dane użytkownika
    assert data["user"]["email"] == "john.doe@example.com"
