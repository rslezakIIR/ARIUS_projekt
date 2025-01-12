import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import '../styles/main.css';

const Profile = () => {
  const { user } = useAuth();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('orders'); // orders or settings

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await fetch('http://localhost:5000/api/orders/user', {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
          },
        });
        if (!response.ok) {
          throw new Error('Nie można pobrać zamówień');
        }
        const data = await response.json();
        setOrders(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  const OrderList = () => (
    <div className="space-y-4">
      {orders.map((order) => (
        <div key={order.id} className="bg-white p-4 rounded-lg shadow">
          <div className="flex justify-between items-center mb-2">
            <span className="font-semibold">Zamówienie #{order.id}</span>
            <span className="text-gray-600">{new Date(order.createdAt).toLocaleDateString()}</span>
          </div>
          <div className="space-y-2">
            {order.items.map((item) => (
              <div key={item.id} className="flex justify-between">
                <span>{item.name} x{item.quantity}</span>
                <span>{item.price * item.quantity} zł</span>
              </div>
            ))}
          </div>
          <div className="mt-4 pt-2 border-t flex justify-between">
            <span className="font-semibold">Suma:</span>
            <span className="font-bold">{order.total} zł</span>
          </div>
        </div>
      ))}
    </div>
  );

  const Settings = () => (
    <div className="bg-white p-6 rounded-lg shadow">
      <h3 className="text-xl font-semibold mb-4">Ustawienia profilu</h3>
      <div className="space-y-4">
        <div>
          <label className="block text-gray-700 mb-2">Email</label>
          <input
            type="email"
            value={user?.email}
            disabled
            className="w-full p-2 border rounded bg-gray-50"
          />
        </div>
        <div>
          <label className="block text-gray-700 mb-2">Imię i nazwisko</label>
          <input
            type="text"
            value={user?.name}
            disabled
            className="w-full p-2 border rounded bg-gray-50"
          />
        </div>
      </div>
    </div>
  );

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold mb-8">Profil użytkownika</h1>

      <div className="mb-6">
        <div className="border-b">
          <nav className="flex space-x-8">
            <button
              onClick={() => setActiveTab('orders')}
              className={`py-4 px-1 ${
                activeTab === 'orders'
                  ? 'border-b-2 border-blue-600 text-blue-600'
                  : 'text-gray-500'
              }`}
            >
              Zamówienia
            </button>
            <button
              onClick={() => setActiveTab('settings')}
              className={`py-4 px-1 ${
                activeTab === 'settings'
                  ? 'border-b-2 border-blue-600 text-blue-600'
                  : 'text-gray-500'
              }`}
            >
              Ustawienia
            </button>
          </nav>
        </div>
      </div>

      {loading ? (
        <div className="text-center py-8">Ładowanie...</div>
      ) : error ? (
        <div className="text-red-500 text-center py-8">{error}</div>
      ) : (
        <div>
          {activeTab === 'orders' ? <OrderList /> : <Settings />}
        </div>
      )}
    </div>
  );
};

export default Profile;