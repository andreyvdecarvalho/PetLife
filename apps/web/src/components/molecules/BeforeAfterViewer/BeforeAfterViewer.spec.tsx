import { render, screen, fireEvent } from '@testing-library/react';
import { BeforeAfterViewer } from './index';
import { describe, it, expect } from 'vitest';
import React from 'react';

describe('BeforeAfterViewer Component', () => {
  const beforeUrl = 'http://example.com/before.jpg';
  const afterUrl = 'http://example.com/after.jpg';

  it('should render before and after images correctly', () => {
    render(<BeforeAfterViewer beforeUrl={beforeUrl} afterUrl={afterUrl} />);

    const beforeImg = screen.getByAltText('Antes do banho e tosa') as HTMLImageElement;
    const afterImg = screen.getByAltText('Depois do banho e tosa') as HTMLImageElement;

    expect(beforeImg).toBeDefined();
    expect(afterImg).toBeDefined();
    expect(beforeImg.src).toBe(beforeUrl);
    expect(afterImg.src).toBe(afterUrl);
  });

  it('should update slider position on change', () => {
    render(<BeforeAfterViewer beforeUrl={beforeUrl} afterUrl={afterUrl} />);

    const slider = screen.getByTestId('before-after-slider') as HTMLInputElement;
    expect(slider.value).toBe('50');

    fireEvent.change(slider, { target: { value: '75' } });
    expect(slider.value).toBe('75');
  });
});
