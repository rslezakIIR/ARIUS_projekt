import pytest
from app import create_app

@pytest.fixture
def client():
    app = create_app()
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_coins_endpoint_accessibility(client):
    # Wysyłamy żądanie GET do /api/coins
    response = client.get('/api/coins/')
    
    # Sprawdzamy, czy kod odpowiedzi to 200 OK
    assert response.status_code == 200
