import React from 'react';
import './Input.css';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  hasError?: boolean;
}

export const Input: React.FC<InputProps> = ({
  hasError = false,
  className = '',
  ...props
}) => {
  const inputClass = `input-glass ${hasError ? 'input-error' : ''} ${className}`;

  return (
    <input
      className={inputClass}
      {...props}
    />
  );
};
