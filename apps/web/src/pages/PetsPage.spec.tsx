import { render } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { PetsPage } from './PetsPage';
import React from 'react';

vi.mock('../components/templates/DashboardLayout', () => ({
  DashboardLayout: ({ children }: any) => <div data-testid="dashboard-layout">{children}</div>
}));

vi.mock('../components/pages/PetsPage', () => ({
  PetsPageContent: () => <div data-testid="pets-page-content" />
}));

describe('PetsPage', () => {
  it('should render PetsPageContent inside DashboardLayout', () => {
    const { getByTestId } = render(<PetsPage />);
    expect(getByTestId('pets-page-content')).toBeDefined();
    expect(getByTestId('dashboard-layout')).toBeDefined();
  });
});
