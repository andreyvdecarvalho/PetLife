import React from 'react';
import './styles.css';

interface SkeletonProps {
  width?: string | number;
  height?: string | number;
  borderRadius?: string | number;
  className?: string;
  variant?: 'rectangular' | 'circular' | 'text';
}

export const Skeleton: React.FC<SkeletonProps> = ({
  width,
  height,
  borderRadius,
  className = '',
  variant = 'text',
}) => {
  const style: React.CSSProperties = {
    width: width || (variant === 'text' ? '100%' : undefined),
    height: height || (variant === 'text' ? '1em' : undefined),
    borderRadius: borderRadius || (variant === 'circular' ? '50%' : undefined),
  };

  return (
    <div
      className={`atom-skeleton atom-skeleton--${variant} ${className}`}
      style={style}
      aria-hidden="true"
    />
  );
};
