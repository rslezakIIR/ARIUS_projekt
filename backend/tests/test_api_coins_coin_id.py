import pytest
from app import create_app

@pytest.fixture
def client():
    app = create_app()
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_get_specific_coin(client):
    # Wysyłamy żądanie GET do /api/coins/1 (przyjmujemy, że moneta o ID 1 istnieje)
    response = client.get('/api/coins/1')

    # Sprawdzamy, czy kod odpowiedzi to 200 OK
    assert response.status_code == 200

    # Pobieramy dane JSON z odpowiedzi
    data = response.get_json()

    # Sprawdzamy, czy odpowiedź zawiera poprawne dane monety
    assert 'id' in data
    assert data['id'] == 1
    assert 'name' in data
    assert 'price' in data
