import { render, screen } from '@testing-library/react';
import { GroomingPage } from './GroomingPage';
import { describe, it, expect, vi } from 'vitest';
import React from 'react';

vi.mock('../components/templates/DashboardLayout', () => ({
  DashboardLayout: ({ children }: { children: React.ReactNode }) => <div data-testid="dashboard-layout">{children}</div>,
}));

vi.mock('../components/pages/GroomingPage', () => ({
  GroomingPageContent: () => <div data-testid="grooming-content">Grooming Content</div>,
}));

describe('GroomingPage Router Wrapper', () => {
  it('should render within DashboardLayout', () => {
    render(<GroomingPage />);
    expect(screen.getByTestId('dashboard-layout')).toBeDefined();
    expect(screen.getByTestId('grooming-content')).toBeDefined();
  });
});
