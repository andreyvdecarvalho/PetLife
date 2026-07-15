import { render } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import { VetDetailPage } from './VetDetailPage';

describe('VetDetailPage', () => {
  it('should render successfully', () => {
    const { container } = render(
      <MemoryRouter>
        <VetDetailPage />
      </MemoryRouter>
    );
    expect(container).toBeDefined();
  });
});
