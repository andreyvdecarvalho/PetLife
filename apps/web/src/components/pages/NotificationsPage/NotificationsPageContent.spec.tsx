import { render, screen, fireEvent } from '@testing-library/react';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { NotificationsPageContent } from '.';
import { useNotifications } from '../../../application/notification/useNotifications';
import React from 'react';

vi.mock('../../../application/notification/useNotifications', () => ({
  useNotifications: vi.fn(),
}));

describe('NotificationsPageContent Component', () => {
  const mockFetchNotifications = vi.fn();
  const mockMarkAsRead = vi.fn();
  const mockMarkAllAsRead = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    (useNotifications as any).mockReturnValue({
      notifications: [
        { id: '1', userId: 'user-1', type: 'VACCINATION_DUE', title: 'Vacina Vencendo', body: 'Vacina da raiva amanhã', read: false, createdAt: '2026-07-08T10:00:00' },
        { id: '2', userId: 'user-1', type: 'SYSTEM', title: 'Aviso do Sistema', body: 'Conta criada', read: true, createdAt: '2026-07-07T12:00:00' },
      ],
      isLoading: false,
      error: null,
      fetchNotifications: mockFetchNotifications,
      markAsRead: mockMarkAsRead,
      markAllAsRead: mockMarkAllAsRead,
    });
  });

  it('should render header and filters correctly', () => {
    render(<NotificationsPageContent />);

    expect(screen.getByText('Central de Notificações')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /não lidas/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /^todas$/i })).toBeInTheDocument();
  });

  it('should show filtered list of notifications', () => {
    render(<NotificationsPageContent />);

    // Under default 'unread' filter, show only unread ones
    expect(screen.getByText('Vacina Vencendo')).toBeInTheDocument();
    expect(screen.queryByText('Aviso do Sistema')).not.toBeInTheDocument();

    // Toggle 'Todas' filter
    fireEvent.click(screen.getByRole('button', { name: /^todas$/i }));
    expect(screen.getByText('Vacina Vencendo')).toBeInTheDocument();
    expect(screen.getByText('Aviso do Sistema')).toBeInTheDocument();
  });

  it('should call markAsRead when checking item check button', () => {
    render(<NotificationsPageContent />);

    const markReadBtn = screen.getByTitle('Marcar como lida');
    fireEvent.click(markReadBtn);

    expect(mockMarkAsRead).toHaveBeenCalledWith('1');
  });

  it('should call markAllAsRead when clicking the top link', () => {
    render(<NotificationsPageContent />);

    const markAllBtn = screen.getByRole('button', { name: /marcar todas como lidas/i });
    fireEvent.click(markAllBtn);

    expect(mockMarkAllAsRead).toHaveBeenCalled();
  });
});
