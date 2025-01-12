import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import '../styles/main.css';

const Login = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    try {
      const success = await login(formData);
      if (success) {
        navigate('/');
      } else {
        setError('Nieprawidłowy email lub hasło');
      }
    } catch (err) {
      setError('Wystąpił błąd podczas logowania');
    }
  };

  return (
    <div className="login">
      <h1 className="login-title">Logowanie</h1>

      {error && (
        <div className="login-error">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="login-form">
        <div className="login-input-group">
          <label className="login-label">Email</label>
          <input
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            className="login-input"
            required
          />
        </div>

        <div className="login-input-group">
          <label className="login-label">Hasło</label>
          <input
            type="password"
            value={formData.password}
            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            className="login-input"
            required
          />
        </div>

        <button type="submit" className="login-button">
          Zaloguj się
        </button>
      </form>

      <p className="login-footer">
        Nie masz konta?{' '}
        <Link to="/register" className="login-link">
          Zarejestruj się
        </Link>
      </p>
    </div>

  );
};

export default Login;