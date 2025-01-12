import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';

const CoinCard = ({ coin }) => {
  const { addToCart } = useCart();

  return (
    <div className="bg-white rounded-lg shadow-md p-4">
      <img
        src={coin.imageUrl}
        alt={coin.name}
        className="w-full h-48 object-contain mb-4"
      />
      <h3 className="text-lg font-semibold">{coin.name}</h3>
      <p className="text-gray-600 text-sm mb-2">{coin.description}</p>
      <div className="text-sm text-gray-500 mb-2">
        <p>Wymiary: {coin.dimensions}</p>
        <p>Stan: {coin.condition}</p>
      </div>
      <div className="mt-4 flex justify-between items-center">
        <span className="text-xl font-bold">{coin.price} zł</span>
        <div className="space-x-2">
          <Link
            to={`/coin/${coin.id}`}
            className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
          >
            Szczegóły
          </Link>
          <button
            onClick={() => addToCart(coin)}
            className="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"
          >
            Do koszyka
          </button>
        </div>
      </div>
    </div>
)}

CoinCard.propTypes = {
  coin: PropTypes.shape({
    imageUrl: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
    dimensions: PropTypes.string.isRequired,
    condition: PropTypes.string.isRequired,
    price: PropTypes.number.isRequired,
    id: PropTypes.string.isRequired,
  }).isRequired,
};

export default CoinCard;export { CoinCard };