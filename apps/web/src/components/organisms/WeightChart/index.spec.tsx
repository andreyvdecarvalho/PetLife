import { render } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { WeightChart } from './index';

describe('WeightChart', () => {
  it('should render successfully with no records', () => {
    const { container } = render(<WeightChart petId="123" records={[]} onRecordAdded={() => {}} />);
    expect(container).toBeDefined();
  });
});
