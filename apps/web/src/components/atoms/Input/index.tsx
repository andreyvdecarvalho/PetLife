import React from 'react';
import './styles.css';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  hasError?: boolean;
  iconLeft?: string;
  rightElement?: React.ReactNode;
}

export const Input: React.FC<InputProps> = ({
  hasError = false,
  className = '',
  iconLeft,
  rightElement,
  ...props
}) => {
  const inputClass = `atom-input ${hasError ? 'atom-input--error' : ''} ${iconLeft ? 'atom-input--with-icon-left' : ''} ${rightElement ? 'atom-input--with-icon-right' : ''} ${className}`;

  return (
    <div className="atom-input-wrapper">
      {iconLeft && (
        <span className="atom-input__icon-left material-symbols-outlined">
          {iconLeft}
        </span>
      )}
      <input className={inputClass} {...props} />
      {rightElement && (
        <div className="atom-input__right-element">
          {rightElement}
        </div>
      )}
    </div>
  );
};
