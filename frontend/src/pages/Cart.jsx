import React from 'react';
import { Link } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import CartItem from '../components/CartItem';

const Cart = () => {
  const { cartItems, getTotal } = useCart();

  if (cartItems.length === 0) {
    return (
      <div className="text-center py-8">
        <h2 className="text-2xl font-bold mb-4">Twój koszyk jest pusty</h2>
        <Link
          to="/collection"
          className="text-blue-600 hover:underline"
        >
          Przejdź do kolekcji
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold mb-8">Koszyk</h1>
      
      <div className="bg-white rounded-lg shadow-md">
        <div className="space-y-4">
          {cartItems.map((item) => (
            <CartItem key={item.id} item={item} />
          ))}
        </div>

        <div className="p-6 border-t">
          <div className="flex justify-between items-center mb-6">
            <span className="text-lg font-semibold">Suma:</span>
            <span className="text-2xl font-bold">{getTotal()} zł</span>
          </div>

          <div className="flex justify-end space-x-4">
            <Link
              to="/collection"
              className="px-6 py-2 text-blue-600 hover:underline"
            >
              Kontynuuj zakupy
            </Link>
            <Link
              to="/checkout"
              className="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700"
            >
              Przejdź do płatności
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;