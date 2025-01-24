import { calculateDiscount } from './helpers';
import React from 'react'; 

test('calculates correct discount', () => {
  const price = 100;
  const discount = 0.2;
  const result = calculateDiscount(price, discount);
  expect(result).toBe(80);
});     




