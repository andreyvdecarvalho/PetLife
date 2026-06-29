import React from 'react';
import './AuthLayout.css';

interface AuthLayoutProps {
  children: React.ReactNode;
}

export const AuthLayout: React.FC<AuthLayoutProps> = ({ children }) => {
  return (
    <div className="auth-layout-wrapper">
      <div className="auth-glow-1" />
      <div className="auth-glow-2" />
      <div className="auth-card">
        <h1 className="auth-logo">🐾 PetLife</h1>
        <p className="auth-tagline">Toda a vida do seu pet em um só lugar</p>
        {children}
      </div>
    </div>
  );
};
