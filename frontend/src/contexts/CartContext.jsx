import React, { createContext, useState, useContext, useEffect } from 'react';

const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const [cartItems, setCartItems] = useState([])

  useEffect(() => {
    // Wczytaj koszyk z localStorage przy starcie
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      setCartItems(JSON.parse(savedCart));
    }
  }, []);

  useEffect(() => {
    // Zapisz koszyk do localStorage przy każdej zmianie
    localStorage.setItem('cart', JSON.stringify(cartItems));
  }, [cartItems]);

  const addToCart = (coin) => {
    setCartItems(prevItems => {
      const existingItem = prevItems.find(item => item.id === coin.id);
      if (existingItem) {
        return prevItems.map(item =>
          item.id === coin.id
            ? { ...item, quantity: item.quantity + 1 }
            : item
        );
      }
      return [...prevItems, { ...coin, quantity: 1 }];
    });
  };

  const removeFromCart = (coinId) => {
    setCartItems(prevItems => prevItems.filter(item => item.id !== coinId));
  };

  const updateQuantity = (coinId, newQuantity) => {
    if (newQuantity < 1) {
      removeFromCart(coinId);
      return;
    }

    setCartItems(prevItems =>
      prevItems.map(item =>
        item.id === coinId
          ? { ...item, quantity: newQuantity }
          : item
      )
    );
  };

  const clearCart = () => {
    setCartItems([]);
  };

  const getTotal = () => {
    return cartItems.reduce(
      (total, item) => total + item.price * item.quantity,
      0
    );
  };

  return (
    <CartContext.Provider
      value={{
        cartItems,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        getTotal,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);