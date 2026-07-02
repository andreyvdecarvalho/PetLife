import React, { useMemo } from 'react';
import { BarChart, Bar, XAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import type { WeightRecordResponse } from '../../../infrastructure/dto/WeightRecordResponse';
import './styles.css';

interface WeightChartProps {
  data: WeightRecordResponse[];
}

export const WeightChart: React.FC<WeightChartProps> = ({ data }) => {
  const chartData = useMemo(() => {
    // Return empty if no data
    if (!data || data.length === 0) return [];

    // Sort ascending by date
    const sorted = [...data].sort((a, b) => new Date(a.recordedAt).getTime() - new Date(b.recordedAt).getTime());
    
    // We want the last 5 records max for display
    const last5 = sorted.slice(-5);

    const formatter = new Intl.DateTimeFormat('pt-BR', { month: 'short' });

    return last5.map((record, index) => {
      const date = new Date(record.recordedAt);
      return {
        label: formatter.format(date).replace('.', ''),
        val: record.weightKg,
        isLatest: index === last5.length - 1
      };
    });
  }, [data]);

  if (chartData.length === 0) {
    return (
      <div className="organism-weight-chart__empty">
        Sem dados de peso para exibir no gráfico.
      </div>
    );
  }

  return (
    <div className="organism-weight-chart">
      <ResponsiveContainer width="100%" height={160}>
        <BarChart data={chartData} margin={{ top: 20, right: 0, left: 0, bottom: 0 }}>
          <XAxis 
            dataKey="label" 
            axisLine={false} 
            tickLine={false} 
            tick={{ fill: 'var(--color-on-surface-variant)', fontSize: 12, fontFamily: 'var(--font-label)' }} 
            dy={10}
          />
          <Tooltip 
            cursor={{ fill: 'rgba(0, 0, 0, 0.05)' }}
            content={({ active, payload }) => {
              if (active && payload && payload.length) {
                return (
                  <div className="organism-weight-chart__tooltip">
                    {payload[0].value} kg
                  </div>
                );
              }
              return null;
            }}
          />
          <Bar dataKey="val" radius={[4, 4, 4, 4]} barSize={32}>
            {chartData.map((entry, index) => (
              <Cell 
                key={`cell-${index}`} 
                fill={entry.isLatest ? 'var(--color-primary)' : 'var(--color-surface-container-high)'} 
              />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};
