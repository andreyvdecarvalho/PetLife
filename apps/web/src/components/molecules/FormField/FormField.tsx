import React from 'react';
import { Label } from '../../atoms/Label/Label';
import { Input } from '../../atoms/Input/Input';
import './FormField.css';

interface FormFieldProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
  required?: boolean;
}

export const FormField: React.FC<FormFieldProps> = ({
  label,
  error,
  required = false,
  id,
  ...props
}) => {
  return (
    <div className="form-field-container">
      <Label htmlFor={id} required={required}>
        {label}
      </Label>
      <Input
        id={id}
        hasError={!!error}
        {...props}
      />
      {error && <span className="error-message">{error}</span>}
    </div>
  );
};
