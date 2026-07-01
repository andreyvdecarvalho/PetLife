import React from 'react';
import './styles.css';

interface AuthLayoutProps {
  children: React.ReactNode;
}

export const AuthLayout: React.FC<AuthLayoutProps> = ({ children }) => {
  return (
    <div className="template-auth-layout">
      {children}
    </div>
  );
};
