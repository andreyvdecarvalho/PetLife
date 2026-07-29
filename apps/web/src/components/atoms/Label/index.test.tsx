import React from 'react';
import { render, screen } from '@testing-library/react';
import { Label } from './index';

describe('Label', () => {
  it('renders children correctly', () => {
    render(<Label>Username</Label>);
    expect(screen.getByText('Username')).toBeInTheDocument();
  });

  it('applies required class when required is true', () => {
    const { container } = render(<Label required>Password</Label>);
    const label = container.querySelector('label');
    expect(label).toHaveClass('atom-label--required');
  });

  it('applies custom className', () => {
    const { container } = render(<Label className="custom-class">Email</Label>);
    const label = container.querySelector('label');
    expect(label).toHaveClass('custom-class');
  });
});
