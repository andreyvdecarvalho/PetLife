import '@testing-library/jest-dom';
import * as matchers from 'vitest-axe/matchers';
import { expect, vi } from 'vitest';
expect.extend(matchers);

// Mock IntersectionObserver
const IntersectionObserverMock = vi.fn(() => ({
  disconnect: vi.fn(),
  observe: vi.fn(),
  takeRecords: vi.fn(),
  unobserve: vi.fn(),
}));
vi.stubGlobal('IntersectionObserver', IntersectionObserverMock);
