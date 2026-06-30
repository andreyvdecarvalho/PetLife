import React from 'react';
import { Label } from '../../atoms/Label';
import { Input } from '../../atoms/Input';
import type { InputProps } from '../../atoms/Input';
import './styles.css';

interface FormFieldProps extends InputProps {
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
    <div className="molecule-form-field">
      <Label htmlFor={id} required={required}>
        {label}
      </Label>
      <Input
        id={id}
        hasError={!!error}
        {...props}
      />
      {error && <span className="molecule-form-field__error">{error}</span>}
    </div>
  );
};
