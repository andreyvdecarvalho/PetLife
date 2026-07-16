import { render, screen } from '@testing-library/react';
import { PetFormPage } from './PetFormPage';
import { vi, describe, it, expect } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import React from 'react';

vi.mock('../components/templates/DashboardLayout', () => ({
  DashboardLayout: ({ children }: any) => <div data-testid="dashboard-layout">{children}</div>,
}));

vi.mock('../components/pages/PetFormPage', () => ({
  PetFormPage: () => <div data-testid="pet-form-page-content">Content</div>,
}));

describe('PetFormPage Wrapper', () => {
  it('should render DashboardLayout and PetFormPageContent', () => {
    render(
      <MemoryRouter>
        <PetFormPage />
      </MemoryRouter>
    );
    expect(screen.getByTestId('dashboard-layout')).toBeDefined();
    expect(screen.getByTestId('pet-form-page-content')).toBeDefined();
  });
});
