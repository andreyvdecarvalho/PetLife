import { renderHook, act } from '@testing-library/react';
import { useGroomingForm } from './useGroomingForm';
import { vi, describe, it, expect } from 'vitest';

describe('useGroomingForm Hook', () => {
  it('should initialize fields correctly', () => {
    const onSubmit = vi.fn();
    const { result } = renderHook(() =>
      useGroomingForm({
        onSubmit,
        initialValues: {
          type: 'GROOMING',
          date: '2026-07-08',
          provider: 'PetShop',
          cost: 120,
          frequencyDays: 30,
          notes: 'Observações importantes',
        },
      })
    );

    expect(result.current.type).toBe('GROOMING');
    expect(result.current.date).toBe('2026-07-08');
    expect(result.current.provider).toBe('PetShop');
    expect(result.current.cost).toBe('120');
    expect(result.current.frequencyDays).toBe('30');
    expect(result.current.notes).toBe('Observações importantes');
  });

  it('should validate form and show error for negative cost', async () => {
    const onSubmit = vi.fn();
    const { result } = renderHook(() =>
      useGroomingForm({
        onSubmit,
      })
    );

    act(() => {
      result.current.setCost('-10');
    });

    const event = { preventDefault: vi.fn() } as any;
    await act(async () => {
      await result.current.handleSubmit(event);
    });

    expect(result.current.errors.cost).toBe('O custo não pode ser negativo.');
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('should validate form and show error for negative/zero frequencyDays', async () => {
    const onSubmit = vi.fn();
    const { result } = renderHook(() =>
      useGroomingForm({
        onSubmit,
      })
    );

    act(() => {
      result.current.setFrequencyDays('0');
    });

    const event = { preventDefault: vi.fn() } as any;
    await act(async () => {
      await result.current.handleSubmit(event);
    });

    expect(result.current.errors.frequencyDays).toBe('A periodicidade deve ser maior que 0 dias.');
    expect(onSubmit).not.toHaveBeenCalled();
  });

  it('should call onSubmit with converted values when valid', async () => {
    const onSubmit = vi.fn().mockResolvedValue(true);
    const { result } = renderHook(() =>
      useGroomingForm({
        onSubmit,
      })
    );

    act(() => {
      result.current.setType('BATH_AND_GROOMING');
      result.current.setDate('2026-07-10');
      result.current.setProvider('Groomer Prime');
      result.current.setCost('150.50');
      result.current.setFrequencyDays('15');
      result.current.setNotes('Cortar unhas');
    });

    const event = { preventDefault: vi.fn() } as any;
    await act(async () => {
      await result.current.handleSubmit(event);
    });

    expect(result.current.errors).toEqual({});
    expect(onSubmit).toHaveBeenCalledWith({
      type: 'BATH_AND_GROOMING',
      date: '2026-07-10',
      provider: 'Groomer Prime',
      cost: 150.5,
      frequencyDays: 15,
      notes: 'Cortar unhas',
    });
  });
});
