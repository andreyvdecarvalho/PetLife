import { render, screen, waitFor } from '@testing-library/react';
import App from './App';
import { vi, describe, it, expect } from 'vitest';
import React from 'react';

vi.mock('./infrastructure/http/api', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    interceptors: {
      request: { use: vi.fn(), eject: vi.fn() },
      response: { use: vi.fn(), eject: vi.fn() }
    }
  },
  setUnauthorizedCallback: vi.fn()
}));

describe('App', () => {
  it('should redirect to login and render login page by default if unauthenticated', async () => {

    render(<App />);
    
    // AuthProvider check will fail since there is no token in localStorage,
    // PrivateRoute will redirect to /login
    // LoginPage and AuthLayout will be rendered
    
    await waitFor(() => {
      expect(screen.getByRole('button', { name: /entrar/i })).toBeDefined();
    });
  });
});
