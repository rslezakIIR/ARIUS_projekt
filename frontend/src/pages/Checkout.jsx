import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../contexts/CartContext';
import { useAuth } from '../contexts/AuthContext';
import OrderSummary from '../components/OrderSummary';
import PaymentForm from '../components/PaymentForm';

const Checkout = () => {
  const { cartItems, getTotal, clearCart } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [shippingDetails, setShippingDetails] = useState({
    address: '',
    city: '',
    postalCode: '',
    phone: '',
  });

  if (!user) {
    navigate('/login');
    return null;
  }

  if (cartItems.length === 0) {
    navigate('/cart');
    return null;
  }

  const handleSubmitOrder = async (paymentMethod) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch('http://localhost:5000/api/orders', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          items: cartItems,
          total: getTotal(),
          shipping: shippingDetails,
          paymentMethod,
        }),
      });

      if (!response.ok) {
        throw new Error('Błąd podczas składania zamówienia');
      }

      clearCart();
      navigate('/profile');
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold mb-8">Zamówienie</h1>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      <div className="grid md:grid-cols-2 gap-8">
        <div className="space-y-6">
          <div className="bg-white p-6 rounded-lg shadow-md">
            <h2 className="text-xl font-semibold mb-4">Dane do wysyłki</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-gray-700 mb-2">Adres</label>
                <input
                  type="text"
                  value={shippingDetails.address}
                  onChange={(e) => setShippingDetails({
                    ...shippingDetails,
                    address: e.target.value
                  })}
                  className="w-full p-2 border rounded"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-700 mb-2">Miasto</label>
                <input
                  type="text"
                  value={shippingDetails.city}
                  onChange={(e) => setShippingDetails({
                    ...shippingDetails,
                    city: e.target.value
                  })}
                  className="w-full p-2 border rounded"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-700 mb-2">Kod pocztowy</label>
                <input
                  type="text"
                  value={shippingDetails.postalCode}
                  onChange={(e) => setShippingDetails({
                    ...shippingDetails,
                    postalCode: e.target.value
                  })}
                  className="w-full p-2 border rounded"
                  required
                />
              </div>
              <div>
                <label className="block text-gray-700 mb-2">Telefon</label>
                <input
                  type="tel"
                  value={shippingDetails.phone}
                  onChange={(e) => setShippingDetails({
                    ...shippingDetails,
                    phone: e.target.value
                  })}
                  className="w-full p-2 border rounded"
                  required
                />
              </div>
            </div>
          </div>

          <PaymentForm
            onSubmit={handleSubmitOrder}
            total={getTotal()}
            disabled={loading}
          />
        </div>

        <div>
          <OrderSummary
            items={cartItems}
            total={getTotal()}
          />
        </div>
      </div>
    </div>
  );
};

export default Checkout;