import { render } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { VetSearchPage } from './VetSearchPage';

vi.mock('../application/veterinarian/useSearchVeterinarians', () => ({
  useSearchVeterinarians: () => ({
    search: vi.fn(),
    results: { content: [], totalPages: 0 },
    loading: false,
    error: null
  })
}));

describe('VetSearchPage', () => {
  it('should render successfully', () => {
    const { container } = render(
      <MemoryRouter>
        <VetSearchPage />
      </MemoryRouter>
    );
    expect(container).toBeDefined();
  });
});
