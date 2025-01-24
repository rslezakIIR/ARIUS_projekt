import { render, screen } from '@testing-library/react';
import Navigation from '../Navigation';
import React from 'react'; 

test('renders navigation links', () => {
  render(<Navigation />);
  expect(screen.getByText('Home')).toBeInTheDocument();
  expect(screen.getByText('Shop')).toBeInTheDocument();
  expect(screen.getByText('Contact')).toBeInTheDocument();
});