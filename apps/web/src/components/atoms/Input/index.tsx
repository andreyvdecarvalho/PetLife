import React from 'react';
import './styles.css';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  hasError?: boolean;
}

export const Input: React.FC<InputProps> = ({
  hasError = false,
  className = '',
  ...props
}) => {
  const inputClass = `atom-input ${hasError ? 'atom-input--error' : ''} ${className}`;

  return (
    <input
      className={inputClass}
      {...props}
    />
  );
};
