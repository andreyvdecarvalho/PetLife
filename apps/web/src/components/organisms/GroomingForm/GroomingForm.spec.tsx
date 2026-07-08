import { render, screen, fireEvent } from '@testing-library/react';
import { GroomingForm } from './index';
import { vi, describe, it, expect } from 'vitest';
import React from 'react';

// Mock the hook dependencies
vi.mock('../../../application/grooming/useGroomingForm', () => ({
  useGroomingForm: ({ initialValues, onSubmit }: any) => {
    return {
      type: initialValues?.type || 'BATH',
      setType: vi.fn(),
      date: initialValues?.date || '2026-07-08',
      setDate: vi.fn(),
      provider: initialValues?.provider || '',
      setProvider: vi.fn(),
      cost: initialValues?.cost || '',
      setCost: vi.fn(),
      frequencyDays: initialValues?.frequencyDays || '',
      setFrequencyDays: vi.fn(),
      notes: initialValues?.notes || '',
      setNotes: vi.fn(),
      errors: {},
      isSubmitting: false,
      handleSubmit: (e: any) => {
        e.preventDefault();
        onSubmit({
          type: initialValues?.type || 'BATH',
          date: initialValues?.date || '2026-07-08',
        });
      },
    };
  },
}));

describe('GroomingForm Component', () => {
  const onSuccess = vi.fn();
  const onCancel = vi.fn();

  it('should render form titles and inputs correctly', () => {
    render(<GroomingForm onSuccess={onSuccess} onCancel={onCancel} />);

    expect(screen.getByText('Registrar Banho e Tosa')).toBeDefined();
    expect(screen.getByTestId('input-date')).toBeDefined();
    expect(screen.getByTestId('input-provider')).toBeDefined();
    expect(screen.getByTestId('input-cost')).toBeDefined();
    expect(screen.getByTestId('input-frequency')).toBeDefined();
  });

  it('should trigger onCancel when cancel button is clicked', () => {
    render(<GroomingForm onSuccess={onSuccess} onCancel={onCancel} />);

    const cancelBtn = screen.getByTestId('btn-cancel');
    fireEvent.click(cancelBtn);

    expect(onCancel).toHaveBeenCalled();
  });

  it('should trigger onSuccess when form is submitted', () => {
    render(<GroomingForm onSuccess={onSuccess} onCancel={onCancel} />);

    const submitBtn = screen.getByTestId('btn-submit');
    fireEvent.click(submitBtn);

    expect(onSuccess).toHaveBeenCalled();
  });
});
