import pytest
from app import create_app

@pytest.fixture
def client():
    app = create_app()
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_auth_logout(client):
    # Logujemy się jako istniejący użytkownik (John Doe z seeda)
    login_response = client.post('/api/auth/login', json={
        "email": "john.doe@example.com",
        "password": "password123"
    })

    # Sprawdzamy, czy logowanie się powiodło
    assert login_response.status_code == 200

    # Pobieramy token z odpowiedzi
    login_data = login_response.get_json()
    assert "access_token" in login_data
    access_token = login_data["access_token"]

    # Wysyłamy żądanie POST do /api/auth/logout z tokenem
    logout_response = client.post('/api/auth/logout', headers={
        "Authorization": f"Bearer {access_token}"
    })

    # Sprawdzamy, czy kod odpowiedzi to 200 OK
    assert logout_response.status_code == 200

    # Sprawdzamy wiadomość w odpowiedzi
    data = logout_response.get_json()
    assert data["message"] == "Wylogowanie zakończone sukcesem"
