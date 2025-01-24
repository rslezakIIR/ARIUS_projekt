import pytest
from app import create_app

@pytest.fixture
def client():
    app = create_app()
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_login_invalid_password(client):
    # Próbujemy zalogować się z błędnym hasłem
    response = client.post('/api/auth/login', json={
        "email": "john.doe@example.com",  # Istniejący email
        "password": "wrongpassword"  # Niepoprawne hasło
    })

    # Sprawdzamy, czy odpowiedź wskazuje na błąd 401 (Unauthorized)
    assert response.status_code == 401
    data = response.get_json()
    assert "error" in data
    assert data["error"] == "Nieprawidłowe dane logowania"
