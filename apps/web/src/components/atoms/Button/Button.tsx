import React from 'react';
import './Button.css';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'premium' | 'secondary' | 'danger' | 'google';
  isLoading?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'premium',
  isLoading = false,
  className = '',
  disabled,
  ...props
}) => {
  const buttonClass = `btn-premium btn-${variant} ${className}`;

  return (
    <button
      className={buttonClass}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading && <div className="spinner" data-testid="button-spinner" />}
      {children}
    </button>
  );
};
