import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Navigation = () => {
  const { user, logout } = useAuth();

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-logo">
          Sklep Numizmatyczny
        </Link>
        <div className="navbar-links">
          <Link to="/collection" className="navbar-link">
            Kolekcja
          </Link>
          {user ? (
            <>
              <Link to="/cart" className="navbar-link">
                Koszyk
              </Link>
              <Link to="/profile" className="navbar-link">
                Profil
              </Link>
              <button onClick={logout} className="navbar-button">
                Wyloguj
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="navbar-link">
                Logowanie
              </Link>
              <Link to="/register" className="navbar-link">
                Rejestracja
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navigation;
