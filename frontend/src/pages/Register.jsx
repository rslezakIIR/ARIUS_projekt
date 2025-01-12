import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/main.css';

const Register = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    name: '',
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError('Hasła nie są identyczne');
      return;
    }

    try {
      const response = await fetch('http://localhost:5000/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: formData.email,
          password: formData.password,
          name: formData.name,
        }),
      });

      if (response.ok) {
        navigate('/login');
      } else {
        const data = await response.json();
        setError(data.message || 'Błąd rejestracji');
      }
    } catch (err) {
      setError('Wystąpił błąd podczas rejestracji');
    }
  };

  return (
    <div className="register">
      <h1 className="register-title">Rejestracja</h1>

      {error && (
        <div className="register-error">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="register-form">
        <div className="register-input-group">
          <label className="register-label">Imię i nazwisko</label>
          <input
            type="text"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className="register-input"
            required
          />
        </div>

        <div className="register-input-group">
          <label className="register-label">Email</label>
          <input
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            className="register-input"
            required
          />
        </div>

        <div className="register-input-group">
          <label className="register-label">Hasło</label>
          <input
            type="password"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            className="register-input"
            required
          />
        </div>

        <div className="register-input-group">
          <label className="register-label">Potwierdź hasło</label>
          <input
            type="password"
            value={formData.confirmPassword}
            onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
            className="register-input"
            required
          />
        </div>

        <button type="submit" className="register-button">
          Zarejestruj się
        </button>
      </form>

      <p className="register-footer">
        Masz już konto?{' '}
        <Link to="/login" className="register-link">
          Zaloguj się
        </Link>
      </p>
    </div>

  );
};

export default Register;