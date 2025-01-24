import pytest
from app import create_app

@pytest.fixture
def client():
    app = create_app()
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_home(client):
    # Wysyłamy żądanie GET do głównego endpointu /
    response = client.get('/')

    # Sprawdzamy, czy kod odpowiedzi to 200 OK
    assert response.status_code == 200

    # Pobieramy dane JSON z odpowiedzi
    data = response.get_json()

    # Sprawdzamy, czy odpowiedź zawiera oczekiwane dane
    assert data == {'status': 'healthy'}
