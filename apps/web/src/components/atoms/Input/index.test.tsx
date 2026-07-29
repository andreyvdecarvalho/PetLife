import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Input } from './index';

describe('Input', () => {
  it('renders correctly', () => {
    render(<Input placeholder="Enter text" />);
    expect(screen.getByPlaceholderText(/enter text/i)).toBeInTheDocument();
  });

  it('applies error class when hasError is true', () => {
    const { container } = render(<Input hasError />);
    const input = container.querySelector('input');
    expect(input).toHaveClass('atom-input--error');
  });

  it('renders left icon correctly', () => {
    render(<Input iconLeft="search" />);
    expect(screen.getByText('search')).toBeInTheDocument();
    expect(screen.getByText('search')).toHaveClass('atom-input__icon-left');
  });

  it('renders right element correctly', () => {
    render(<Input rightElement={<button>Clear</button>} />);
    expect(screen.getByRole('button', { name: /clear/i })).toBeInTheDocument();
  });

  it('handles onChange event', () => {
    const handleChange = vi.fn();
    render(<Input placeholder="Type here" onChange={handleChange} />);
    const input = screen.getByPlaceholderText(/type here/i);
    fireEvent.change(input, { target: { value: 'test' } });
    expect(handleChange).toHaveBeenCalledTimes(1);
  });
});
