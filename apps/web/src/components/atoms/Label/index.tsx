import React from 'react';
import './styles.css';

interface LabelProps extends React.LabelHTMLAttributes<HTMLLabelElement> {
  required?: boolean;
}

export const Label: React.FC<LabelProps> = ({
  children,
  required = false,
  className = '',
  ...props
}) => {
  const labelClass = `atom-label ${required ? 'atom-label--required' : ''} ${className}`;

  return (
    <label className={labelClass} {...props}>
      {children}
    </label>
  );
};
