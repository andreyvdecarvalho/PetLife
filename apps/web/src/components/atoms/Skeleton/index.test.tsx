import React from 'react';
import { render, screen } from '@testing-library/react';
import { Skeleton } from './index';

describe('Skeleton', () => {
  it('renders correctly with default props', () => {
    const { container } = render(<Skeleton />);
    const skeletonElement = container.querySelector('.atom-skeleton');
    
    expect(skeletonElement).toBeInTheDocument();
    expect(skeletonElement).toHaveClass('atom-skeleton--text');
    expect(skeletonElement).toHaveAttribute('aria-hidden', 'true');
    expect(skeletonElement).toHaveStyle({ width: '100%', height: '1em' });
  });

  it('renders with rectangular variant', () => {
    const { container } = render(<Skeleton variant="rectangular" width={200} height={100} />);
    const skeletonElement = container.querySelector('.atom-skeleton');
    
    expect(skeletonElement).toHaveClass('atom-skeleton--rectangular');
    expect(skeletonElement).toHaveStyle({ width: '200px', height: '100px' });
  });

  it('renders with circular variant', () => {
    const { container } = render(<Skeleton variant="circular" width={50} height={50} />);
    const skeletonElement = container.querySelector('.atom-skeleton');
    
    expect(skeletonElement).toHaveClass('atom-skeleton--circular');
    expect(skeletonElement).toHaveStyle({ width: '50px', height: '50px', borderRadius: '50%' });
  });

  it('applies custom className and borderRadius', () => {
    const { container } = render(<Skeleton className="custom-class" borderRadius="8px" />);
    const skeletonElement = container.querySelector('.atom-skeleton');
    
    expect(skeletonElement).toHaveClass('custom-class');
    expect(skeletonElement).toHaveStyle({ borderRadius: '8px' });
  });
});
