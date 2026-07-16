import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../../contexts/AuthContext';
import './styles.css';

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

  const isActive = (path: string) => {
    if (path === '/') {
      return location.pathname === '/';
    }
    return location.pathname.startsWith(path);
  };

  return (
    <div className="template-dashboard">
      {/* TopAppBar */}
      <header className="template-dashboard__header">
        <div className="template-dashboard__header-container">
          <div className="template-dashboard__logo">
            <span className="material-symbols-outlined filled">pets</span>
            <span className="template-dashboard__logo-text">PetLife</span>
          </div>
          <div className="template-dashboard__header-actions">
            <button className="template-dashboard__action-btn" title="Notificações">
              <span className="material-symbols-outlined">notifications</span>
            </button>
            
            {user && (
              <div className="template-dashboard__user-menu">
                {user.avatarUrl ? (
                  <img src={user.avatarUrl} alt={user.nickname || user.name} className="template-dashboard__user-avatar" />
                ) : (
                  <div className="template-dashboard__user-avatar-placeholder">
                    <span className="material-symbols-outlined">person</span>
                  </div>
                )}
                <span className="template-dashboard__user-name">{user.nickname?.trim() || user.name?.split(' ')[0]}</span>
                <button className="template-dashboard__logout-btn" onClick={handleLogout} title="Sair">
                  <span className="material-symbols-outlined">logout</span>
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Main Container */}
      <div className="template-dashboard__container">
        {/* Desktop Sidebar Nav */}
        <aside className="template-dashboard__sidebar">
          <nav className="template-dashboard__sidebar-nav">
            <Link
              to="/"
              className={`template-dashboard__sidebar-link ${isActive('/') ? 'active' : ''}`}
            >
              <span className="material-symbols-outlined">dashboard</span>
              Início
            </Link>
            <Link
              to="/profile"
              className={`template-dashboard__sidebar-link ${isActive('/profile') ? 'active' : ''}`}
            >
              <span className="material-symbols-outlined">person</span>
              Meu Perfil
            </Link>
            <Link
              to="/medications"
              className={`template-dashboard__sidebar-link ${isActive('/medications') ? 'active' : ''}`}
            >
              <span className="material-symbols-outlined">pill</span>
              Medicamentos
            </Link>
            <Link
              to="/routine"
              className={`template-dashboard__sidebar-link ${isActive('/routine') ? 'active' : ''}`}
            >
              <span className="material-symbols-outlined">calendar_today</span>
              Rotina
            </Link>
            <Link
              to="/appointments"
              className={`template-dashboard__sidebar-link ${isActive('/appointments') ? 'active' : ''}`}
            >
              <span className="material-symbols-outlined">event</span>
              Agendamentos
            </Link>
            <Link
              to="/veterinarians"
              className={`template-dashboard__sidebar-link ${isActive('/veterinarians') ? 'active' : ''}`}
            >
              <span className="material-symbols-outlined">medical_services</span>
              Veterinários
            </Link>
            <Link
              to="/memories"
              className={`template-dashboard__sidebar-link ${isActive('/memories') ? 'active' : ''}`}
            >
              <span className="material-symbols-outlined">photo_library</span>
              Memórias
            </Link>
          </nav>
        </aside>

        {/* Content Area */}
        <main className="template-dashboard__content">
          {children}
        </main>
      </div>

      {/* Mobile Bottom Navigation */}
      <nav className="template-dashboard__bottom-nav">
        <Link
          to="/"
          className={`template-dashboard__bottom-nav-link ${location.pathname === '/' ? 'active' : ''}`}
        >
          <span className="material-symbols-outlined">dashboard</span>
          <span className="template-dashboard__bottom-nav-label">Início</span>
        </Link>
        <Link
          to="/profile"
          className={`template-dashboard__bottom-nav-link ${location.pathname === '/profile' ? 'active' : ''}`}
        >
          <span className="material-symbols-outlined">person</span>
          <span className="template-dashboard__bottom-nav-label">Perfil</span>
        </Link>
        <Link
          to="/medications"
          className={`template-dashboard__bottom-nav-link ${location.pathname.startsWith('/medications') ? 'active' : ''}`}
        >
          <span className="material-symbols-outlined">pill</span>
          <span className="template-dashboard__bottom-nav-label">Remédios</span>
        </Link>
        <Link
          to="/routine"
          className={`template-dashboard__bottom-nav-link ${location.pathname.startsWith('/routine') ? 'active' : ''}`}
        >
          <span className="material-symbols-outlined">calendar_today</span>
          <span className="template-dashboard__bottom-nav-label">Rotina</span>
        </Link>
        <Link
          to="/memories"
          className={`template-dashboard__bottom-nav-link ${location.pathname.startsWith('/memories') ? 'active' : ''}`}
        >
          <span className="material-symbols-outlined">photo_library</span>
          <span className="template-dashboard__bottom-nav-label">Memórias</span>
        </Link>
      </nav>
    </div>
  );
};
