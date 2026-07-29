import React from 'react';
import { render, screen } from '@testing-library/react';
import { FormField } from './index';
import { vi } from 'vitest';

describe('FormField', () => {
  it('renders label and input correctly', () => {
    render(<FormField id="email" label="Email Address" placeholder="Enter email" />);
    
    expect(screen.getByText('Email Address')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter email')).toBeInTheDocument();
  });

  it('renders required indicator on label if required', () => {
    const { container } = render(<FormField id="name" label="Name" required />);
    
    const label = container.querySelector('label');
    expect(label).toHaveClass('atom-label--required');
  });

  it('renders error message and passes hasError to input', () => {
    const { container } = render(<FormField id="pw" label="Password" error="Too short" />);
    
    expect(screen.getByText('Too short')).toBeInTheDocument();
    expect(screen.getByText('Too short')).toHaveClass('molecule-form-field__error');
    
    const input = container.querySelector('input');
    expect(input).toHaveClass('atom-input--error');
  });
});
