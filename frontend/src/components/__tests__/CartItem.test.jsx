import { render, screen } from '@testing-library/react';
import CartItem from '../CartItem';
import React from 'react'; 

test('renders CartItem with product name and price', () => {
  render(<CartItem name="Product A" price={29.99} />);
  expect(screen.getByText('Product A')).toBeInTheDocument();
  expect(screen.getByText('$29.99')).toBeInTheDocument();
});