import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

const CoinDetail = () => {
  const { id } = useParams(); // Pobranie ID z adresu URL
  const [coin, setCoin] = useState(null); // Dane szczegółowe monety
  const [loading, setLoading] = useState(true); // Status ładowania
  const [error, setError] = useState(null); // Obsługa błędów

  useEffect(() => {
    const fetchCoin = async () => {
      try {
        console.log(`Pobieranie danych monety o ID: ${id}`);
        const response = await fetch(`http://localhost:5000/api/coins/${id}`);
        if (!response.ok) {
          throw new Error(`Błąd HTTP: ${response.status}`);
        }
        const data = await response.json();
        setCoin(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchCoin();
  }, [id]);

  if (loading) {
    return <div className="text-center py-8">Ładowanie...</div>;
  }

  if (error) {
    return <div className="text-center text-red-500 py-8">Błąd: {error}</div>;
  }

  if (!coin) {
    return <div className="text-center py-8">Nie znaleziono monety</div>;
  }

  return (
    <div className="coin-detail-container">
      <h1 className="coin-name">{coin.name}</h1>
      <img
        src={coin.image_url}
        alt={coin.name}
        className="coin-image"
        style={{ width: '100%', maxHeight: '300px', objectFit: 'contain' }}
      />
      <p>{coin.description}</p>
      <p>
        <strong>Kraj:</strong> {coin.country}
      </p>
      <p>
        <strong>Rok wydania:</strong> {coin.year_issued}
      </p>
      <p>
        <strong>Cena:</strong> {coin.price} zł
      </p>
      <p>
        <strong>Ilość dostępna:</strong> {coin.quantity_available}
      </p>
      <p>
        <strong>Średnia ocena:</strong> {coin.average_rating} (na podstawie {coin.reviews_count} recenzji)
      </p>
    </div>
  );
};

export default CoinDetail;
