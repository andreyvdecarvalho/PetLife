import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { vi } from 'vitest';
import { Button } from './index';
describe('Button', () => {
  it('renders children correctly', () => {
    render(<Button>Click Me</Button>);
    expect(screen.getByRole('button', { name: /click me/i })).toBeInTheDocument();
  });

  it('renders with primary variant by default', () => {
    const { container } = render(<Button>Primary Button</Button>);
    const button = container.querySelector('button');
    expect(button).toHaveClass('atom-button--primary');
  });

  it('renders with secondary variant', () => {
    const { container } = render(<Button variant="secondary">Secondary Button</Button>);
    const button = container.querySelector('button');
    expect(button).toHaveClass('atom-button--secondary');
  });

  it('renders with danger variant', () => {
    const { container } = render(<Button variant="danger">Danger Button</Button>);
    const button = container.querySelector('button');
    expect(button).toHaveClass('atom-button--danger');
  });

  it('renders spinner when isLoading is true and disables button', () => {
    render(<Button isLoading>Loading...</Button>);
    expect(screen.getByTestId('button-spinner')).toBeInTheDocument();
    expect(screen.getByRole('button')).toBeDisabled();
  });

  it('handles click events', () => {
    const handleClick = vi.fn();
    render(<Button onClick={handleClick}>Clickable</Button>);
    fireEvent.click(screen.getByRole('button', { name: /clickable/i }));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('is disabled when disabled prop is passed', () => {
    const handleClick = vi.fn();
    render(<Button disabled onClick={handleClick}>Disabled</Button>);
    const button = screen.getByRole('button', { name: /disabled/i });
    expect(button).toBeDisabled();
    fireEvent.click(button);
    expect(handleClick).not.toHaveBeenCalled();
  });
});
