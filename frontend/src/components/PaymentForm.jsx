import { useState } from 'react';
import PropTypes from 'prop-types';

const PaymentForm = ({ onSubmit, total }) => {
  const [paymentMethod, setPaymentMethod] = useState('card');

  const handleSubmit = (e) => {
    e.preventDefault();
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-xl font-semibold mb-4">Płatność</h2>
      <div className="mb-4">
        <p className="text-lg">Suma do zapłaty: {total} zł</p>
      </div>
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block mb-2">Metoda płatności</label>
          <select
            value={paymentMethod}
            onChange={(e) => setPaymentMethod(e.target.value)}
            className="w-full p-2 border rounded"
          >
            <option value="card">Karta płatnicza</option>
            <option value="transfer">Przelew</option>
            <option value="blik">BLIK</option>
          </select>
        </div>
        <button
          type="submit"
          className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600"
        >
          Zapłać
        </button>
      </form>
    </div>
  );
};

PaymentForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  total: PropTypes.number.isRequired,
};

export default PaymentForm;
