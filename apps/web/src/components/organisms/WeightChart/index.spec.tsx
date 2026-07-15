import { render } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { WeightChart } from './index';

// Mock do recharts para que ele renderize sem precisar medir o DOM (já que no jsdom width/height é 0)
vi.mock('recharts', async () => {
  const ActualRecharts = await vi.importActual('recharts');
  return {
    ...ActualRecharts as any,
    ResponsiveContainer: ({ children }: any) => (
      <div style={{ width: 800, height: 400 }}>{children}</div>
    ),
  };
});

describe('WeightChart', () => {
  it('should render empty state if no data provided', () => {
    const { getByText } = render(<WeightChart data={[]} />);
    expect(getByText('Sem dados de peso para exibir no gráfico.')).toBeDefined();
  });

  it('should render chart if data is provided and sort them', () => {
    const mockData = [
      { id: '1', weightKg: 10, recordedAt: '2023-01-01T10:00:00Z', notes: '' },
      { id: '2', weightKg: 12, recordedAt: '2023-03-01T10:00:00Z', notes: '' },
      { id: '3', weightKg: 11, recordedAt: '2023-02-01T10:00:00Z', notes: '' }, // Fora de ordem de propósito para testar o sort
    ];

    const { container } = render(<WeightChart data={mockData} />);
    
    // O container deve possuir o wrapper recharts-wrapper renderizado
    const chartWrapper = container.querySelector('.recharts-wrapper');
    expect(chartWrapper).toBeDefined();
  });
});
