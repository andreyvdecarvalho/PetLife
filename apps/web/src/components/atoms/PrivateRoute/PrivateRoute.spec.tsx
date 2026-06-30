import { render } from '@testing-library/react';
import { PrivateRoute } from './PrivateRoute';
import { useAuth } from '../../../contexts/AuthContext';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { vi, describe, it, expect, Mock } from 'vitest';
import React from 'react';

vi.mock('../../../contexts/AuthContext');

describe('PrivateRoute', () => {
  it('should render children if authenticated', () => {
    (useAuth as Mock).mockReturnValue({ isAuthenticated: true, loading: false });

    const { getByText } = render(
      <MemoryRouter>
        <PrivateRoute>
          <div>Protected Content</div>
        </PrivateRoute>
      </MemoryRouter>
    );

    expect(getByText('Protected Content')).toBeDefined();
  });

  it('should redirect to login if not authenticated', () => {
    (useAuth as Mock).mockReturnValue({ isAuthenticated: false, loading: false });

    const { getByText } = render(
      <MemoryRouter initialEntries={['/private']}>
        <Routes>
          <Route path="/private" element={<PrivateRoute><div>Protected</div></PrivateRoute>} />
          <Route path="/login" element={<div>Login Page</div>} />
        </Routes>
      </MemoryRouter>
    );

    expect(getByText('Login Page')).toBeDefined();
  });

  it('should show nothing (or loading spinner) while loading', () => {
    (useAuth as Mock).mockReturnValue({ isAuthenticated: false, loading: true });

    const { queryByText } = render(
      <MemoryRouter>
        <PrivateRoute>
          <div>Protected Content</div>
        </PrivateRoute>
      </MemoryRouter>
    );

    expect(queryByText('Protected Content')).toBeNull();
    expect(queryByText('Login Page')).toBeNull();
  });
});
