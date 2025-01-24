import pytest
from app import create_app

@pytest.fixture
def client():
    app = create_app()
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_search_coins_filter_by_country(client):
    # Wysyłamy żądanie GET do /api/coins/search z parametrem filtrowania country
    response = client.get('/api/coins/search', query_string={"country": "USA"})
    
    # Sprawdzamy, czy kod odpowiedzi to 200 OK
    assert response.status_code == 200

    # Pobieramy dane JSON z odpowiedzi
    data = response.get_json()

    # Sprawdzamy, czy odpowiedź zawiera monety z odpowiednim krajem
    assert 'coins' in data
    for coin in data['coins']:
        assert coin['country'] == "USA"
