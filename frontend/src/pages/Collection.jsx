import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import '../styles/main.css';

const Collection = () => {
  const [coins, setCoins] = useState([]);

  useEffect(() => {
    fetch('http://localhost:5000/api/coins')
      .then((response) => {
        if (!response.ok) {
          console.error(`Server returned ${response.status}: ${response.statusText}`);
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        console.log('Fetched data:', data);
        setCoins(data.coins); // Adjust based on backend response structure
      })
      .catch((error) => console.error('Error fetching coins:', error));
  }, []);

  return (
    <div className="collection">
      <h1 className="collection-title">Kolekcja monet</h1>
      {coins && coins.length > 0 ? (
        <div className="collection-grid">
          {coins.map((coin) => (
            <div key={coin.id} className="collection-item">
              <Link to={`/coins/${coin.id}`} className="collection-item-link">
                <img
                  src={coin.image_url}
                  alt={coin.name}
                  className="collection-item-image"
                />
              </Link>
              <h3 className="collection-item-name">{coin.name}</h3>
              <p className="collection-item-description">{coin.description}</p>
              <p className="collection-item-price">{coin.price} zł</p>
              <Link to={`/coins/${coin.id}`} className="collection-item-link">
                Więcej informacji
              </Link>
            </div>
          ))}
        </div>
      ) : (
        <p className="collection-empty">Nie znaleziono monet</p>
      )}
    </div>
  );
};

export default Collection;
