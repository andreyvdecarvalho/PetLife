import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { OnboardingPage } from './OnboardingPage';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi, describe, it, expect } from 'vitest';
import React from 'react';

describe('OnboardingPage', () => {
  it('should render correctly with title, illustration, and CTA button', () => {
    render(
      <MemoryRouter initialEntries={['/onboarding']}>
        <Routes>
          <Route path="/onboarding" element={<OnboardingPage />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('PetLife')).toBeDefined();
    expect(screen.getByText('Vamos conhecer o seu melhor amigo?')).toBeDefined();
    expect(screen.getByText('Adicione os detalhes do seu pet para começarmos a acompanhar a saúde dele de perto.')).toBeDefined();
    expect(screen.getByRole('button', { name: /começar/i })).toBeDefined();
  });

  it('should redirect to /register when clicking the Começar button', async () => {
    let testLocation: any;

    render(
      <MemoryRouter initialEntries={['/onboarding']}>
        <Routes>
          <Route path="/onboarding" element={<OnboardingPage />} />
          <Route path="/register" element={<div data-testid="register-screen">Register Page</div>} />
        </Routes>
      </MemoryRouter>
    );

    const startBtn = screen.getByRole('button', { name: /começar/i });
    fireEvent.click(startBtn);

    await waitFor(() => {
      expect(screen.getByTestId('register-screen')).toBeDefined();
    });
  });
});
