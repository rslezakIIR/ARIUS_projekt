import { Link } from 'react-router-dom';
import { useCoins } from '../contexts/CoinsContext';
import { CoinCard } from '../components/CoinCard';

const Home = () => {
  const { coins, loading } = useCoins();
  const featuredCoins = Array.isArray(coins) ? coins.slice(0, 3) : []; // Display first 3 coins as featured

  return (
    <div className="home">
      <div className="home-header">
        <h1 className="home-title">Sklep Numizmatyczny</h1>
        <p className="home-subtitle">
          Odkryj naszą wyjątkową kolekcję monet
        </p>
      </div>

      <div className="home-banner">
        <h2 className="home-banner-title">Witamy w naszym sklepie</h2>
        <p className="home-banner-text">Znajdź monety, których szukasz w naszej bogatej kolekcji</p>
        <Link to="/collection" className="home-banner-button">
          Zobacz kolekcję
        </Link>
      </div>

      <div className="home-featured">
        <h2 className="home-featured-title">Wyróżnione monety</h2>
        {loading ? (
          <div className="home-loading">Ładowanie...</div>
        ) : (
          <div className="home-coins">
            {featuredCoins.map((coin) => (
              <CoinCard key={coin.id} coin={coin} />
            ))}
          </div>
        )}
      </div>
    </div>

  );
};

export default Home;