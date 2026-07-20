import { render } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { VaccinesPage } from './VaccinesPage';
import React from 'react';

vi.mock('../components/templates/DashboardLayout', () => ({
  DashboardLayout: ({ children }: any) => <div data-testid="dashboard-layout">{children}</div>
}));

vi.mock('../components/pages/VaccinesPage', () => ({
  VaccinesPage: () => <div data-testid="vaccines-page-content" />
}));

describe('VaccinesPage wrapper', () => {
  it('should render VaccinesPage from components', () => {
    const { getByTestId } = render(<VaccinesPage />);
    expect(getByTestId('vaccines-page-content')).toBeDefined();
    expect(getByTestId('dashboard-layout')).toBeDefined();
  });
});
