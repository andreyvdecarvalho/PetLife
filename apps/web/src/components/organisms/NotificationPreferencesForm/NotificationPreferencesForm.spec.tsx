import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { NotificationPreferencesForm } from '.';
import { useNotificationPreferences } from '../../../application/notification/useNotificationPreferences';
import { useToast } from '../../molecules/Toast';
import React from 'react';

vi.mock('../../../application/notification/useNotificationPreferences', () => ({
  useNotificationPreferences: vi.fn(),
}));

vi.mock('../../molecules/Toast', () => ({
  useToast: vi.fn(),
}));

describe('NotificationPreferencesForm Component', () => {
  const mockFetchPreferences = vi.fn();
  const mockSavePreferences = vi.fn();
  const mockShowToast = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useNotificationPreferences as any).mockReturnValue({
      preferences: {
        pushEnabled: true,
        emailEnabled: true,
        vaccines: true,
        medications: true,
        appointments: true,
        grooming: true,
        marketing: false,
        doNotDisturbStart: '22:00:00',
        doNotDisturbEnd: '07:00:00',
      },
      isLoading: false,
      isSaving: false,
      fetchPreferences: mockFetchPreferences,
      savePreferences: mockSavePreferences,
    });
    (useToast as any).mockReturnValue({
      showToast: mockShowToast,
    });
  });

  it('should render form fields correctly', () => {
    render(<NotificationPreferencesForm />);

    expect(screen.getByLabelText(/notificações push/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/notificações por e-mail/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/vacinas/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/medicamentos/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /salvar preferências/i })).toBeInTheDocument();
  });

  it('should call savePreferences on submit', async () => {
    mockSavePreferences.mockResolvedValue(true);
    render(<NotificationPreferencesForm />);

    const submitBtn = screen.getByRole('button', { name: /salvar preferências/i });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(mockSavePreferences).toHaveBeenCalledWith({
        pushEnabled: true,
        emailEnabled: true,
        vaccines: true,
        medications: true,
        appointments: true,
        grooming: true,
        marketing: false,
        doNotDisturbStart: '22:00:00',
        doNotDisturbEnd: '07:00:00',
      });
      expect(mockShowToast).toHaveBeenCalledWith('Preferências de notificação salvas com sucesso! 🔔', 'success');
    });
  });

  it('should render loading state when isLoading is true', () => {
    (useNotificationPreferences as any).mockReturnValue({
      preferences: null,
      isLoading: true,
      isSaving: false,
      fetchPreferences: mockFetchPreferences,
      savePreferences: mockSavePreferences,
    });

    render(<NotificationPreferencesForm />);
    expect(screen.getByText(/carregando configurações.../i)).toBeInTheDocument();
  });
});
