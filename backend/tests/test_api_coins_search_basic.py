import pytest
from app import create_app


@pytest.fixture
def client():
    app = create_app()
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client



def test_search_coins(client):
    # Wysyłamy żądanie GET do /api/coins/search z parametrem query
    response = client.get('/api/coins/search?q=Test')

    # Sprawdzamy, czy kod odpowiedzi to 200 OK
    assert response.status_code == 200

    # Pobieramy dane JSON z odpowiedzi
    data = response.get_json()

    # Sprawdzamy, czy odpowiedź zawiera listę monet
    assert 'coins' in data
    assert isinstance(data['coins'], list)
