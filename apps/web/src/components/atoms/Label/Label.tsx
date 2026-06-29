import React from 'react';
import './Label.css';

interface LabelProps extends React.LabelHTMLAttributes<HTMLLabelElement> {
  required?: boolean;
}

export const Label: React.FC<LabelProps> = ({
  children,
  required = false,
  className = '',
  ...props
}) => {
  const labelClass = `label-premium ${required ? 'label-required' : ''} ${className}`;

  return (
    <label className={labelClass} {...props}>
      {children}
    </label>
  );
};
