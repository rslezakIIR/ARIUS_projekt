import { useState } from 'react';
import PropTypes from 'prop-types';

const Rating = ({ initialRating = 0, onRate, readonly = false }) => {
  const [rating, setRating] = useState(initialRating);
  const [hover, setHover] = useState(0);

  const handleRate = (value) => {
    if (!readonly) {
      setRating(value);
      if (onRate) {
        onRate(value);
    }
  }
  };

  return (
    <div className="flex items-center space-x-1">
      {[1, 2, 3, 4, 5].map((star) => (
        <button
          key={star}
          className={`text-2xl ${
            readonly
              ? 'cursor-default'
              : 'cursor-pointer'
          }`}
          onClick={() => handleRate(star)}
          onMouseEnter={() => !readonly && setHover(star)}
          onMouseLeave={() => !readonly && setHover(0)}
        >
          {star <= (hover || rating) ? '★' : '☆'}
        </button>
      ))}
    </div>
  );
};

Rating.propTypes = {
  initialRating: PropTypes.number,
  onRate: PropTypes.func,
  readonly: PropTypes.bool,
};

export default Rating;