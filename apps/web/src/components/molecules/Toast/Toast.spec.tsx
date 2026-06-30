import { render, fireEvent, act } from '@testing-library/react';
import { ToastProvider, useToast } from '.';
import { vi, describe, it, expect } from 'vitest';
import React from 'react';

const TestComponent = () => {
  const { showToast } = useToast();
  return (
    <div>
      <button onClick={() => showToast('Success message', 'success')}>Show Success</button>
      <button onClick={() => showToast('Error message', 'error')}>Show Error</button>
    </div>
  );
};

describe('ToastProvider', () => {
  it('should show toast message when showToast is called', () => {
    const { getByText, getByTestId } = render(
      <ToastProvider>
        <TestComponent />
      </ToastProvider>
    );

    fireEvent.click(getByText('Show Success'));
    expect(getByText('Success message')).toBeDefined();
    expect(getByTestId('toast-success')).toBeDefined();

    fireEvent.click(getByText('Show Error'));
    expect(getByText('Error message')).toBeDefined();
    expect(getByTestId('toast-error')).toBeDefined();
  });

  it('should auto close toast after 3 seconds', () => {
    vi.useFakeTimers();
    const { getByText, queryByText } = render(
      <ToastProvider>
        <TestComponent />
      </ToastProvider>
    );

    fireEvent.click(getByText('Show Success'));
    expect(getByText('Success message')).toBeDefined();

    act(() => {
      vi.advanceTimersByTime(3000);
    });

    expect(queryByText('Success message')).toBeNull();
    vi.useRealTimers();
  });

  it('should throw error if useToast is used outside ToastProvider', () => {
    const consoleError = vi.spyOn(console, 'error').mockImplementation(() => {});
    
    expect(() => render(<TestComponent />)).toThrow('useToast deve ser usado com um ToastProvider');
    
    consoleError.mockRestore();
  });
});
