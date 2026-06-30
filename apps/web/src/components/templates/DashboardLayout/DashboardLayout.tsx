import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../../contexts/AuthContext';
import { Button } from '../../atoms/Button/Button';
import './DashboardLayout.css';

interface DashboardLayoutProps {
  children: React.ReactNode;
}

export const DashboardLayout: React.FC<DashboardLayoutProps> = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="dashboard-wrapper">
      <header className="dashboard-header">
        <Link to="/" className="dashboard-logo">
          🐾 PetLife
        </Link>

        <nav className="dashboard-nav">
          <Link
            to="/"
            className={`nav-link-premium ${location.pathname === '/' ? 'active' : ''}`}
          >
            Dashboard
          </Link>
          <Link
            to="/profile"
            className={`nav-link-premium ${location.pathname === '/profile' ? 'active' : ''}`}
          >
            Meu Perfil
          </Link>

          {user && (
            <div className="user-profile-menu">
              {user.avatarUrl ? (
                <img src={user.avatarUrl} alt="Avatar" className="user-avatar-btn" />
              ) : (
                <div
                  className="user-avatar-btn"
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    background: 'rgba(255, 255, 255, 0.05)',
                    fontSize: '18px',
                    cursor: 'pointer'
                  }}
                >
                  👤
                </div>
              )}
              <span style={{ fontSize: '14px', fontWeight: 500 }}>{user.name}</span>
            </div>
          )}

          <Button
            variant="secondary"
            onClick={handleLogout}
            style={{ width: 'auto', padding: '8px 16px', fontSize: '14px' }}
          >
            Sair
          </Button>
        </nav>
      </header>

      <main className="dashboard-content">{children}</main>
    </div>
  );
};
