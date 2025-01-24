import pytest
from app import create_app

@pytest.fixture
def client():
    app = create_app()
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_register_existing_user(client):
    # Wysyłamy żądanie rejestracji z danymi istniejącego użytkownika
    response = client.post('/api/auth/register', json={
        "email": "john.doe@example.com",  # Email istniejącego użytkownika
        "name": "JohnDoe",  # Nazwa użytkownika
        "password": "password123"
    })

    # Sprawdzamy, czy odpowiedź wskazuje na konflikt (400 Bad Request)
    assert response.status_code == 400
    data = response.get_json()
    assert "error" in data
    assert data["error"] == "Email już istnieje"  # Sprawdzamy komunikat błędu
