import React from 'react';

const OrderSummary = ({ items, total }) => {
  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-xl font-semibold mb-4">Podsumowanie zamówienia</h2>
      <div className="space-y-4">
        {items.map((item) => (
          <div key={item.id} className="flex justify-between">
            <div>
              <p className="font-medium">{item.name}</p>
              <p className="text-sm text-gray-600">Ilość: {item.quantity}</p>
            </div>
            <p className="font-medium">{item.price * item.quantity} zł</p>
          </div>
        ))}
        <div className="border-t pt-4">
          <div className="flex justify-between font-bold">
            <p>Suma</p>
            <p>{total} zł</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderSummary;