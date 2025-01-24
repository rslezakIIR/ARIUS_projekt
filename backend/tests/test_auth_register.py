import pytest
from app import create_app

@pytest.fixture
def client():
    app = create_app()
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_auth_register_endpoint(client):
    # Wysyłamy żądanie POST do /api/auth/register z danymi nowego użytkownika
    response = client.post('/api/auth/register', json={
        "email": "new.user21@example.com",  # Przykładowy nowy email
        "name": "NewUser21",               # Przykładowa nazwa użytkownika
        "password": "securepassword12321"  # Przykładowe hasło
    })

    # Sprawdzamy, czy kod odpowiedzi to 201 Created
    assert response.status_code == 201

    # Pobieramy dane JSON z odpowiedzi
    data = response.get_json()

    # Sprawdzamy, czy odpowiedź zawiera poprawną wiadomość
    assert "message" in data
    assert data["message"] == "Rejestracja zakończona sukcesem"
