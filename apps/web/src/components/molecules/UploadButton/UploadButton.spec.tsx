import { render, screen, fireEvent } from '@testing-library/react';
import { UploadButton } from './index';
import { vi, describe, it, expect } from 'vitest';
import React from 'react';

describe('UploadButton Component', () => {
  it('should render placeholder when no photo preview is provided', () => {
    render(
      <UploadButton
        photoPreview={null}
        compressing={false}
        onPhotoChange={vi.fn()}
      />
    );
    expect(screen.getByText('Adicionar Foto')).toBeDefined();
  });

  it('should render compressing state when loading', () => {
    render(
      <UploadButton
        photoPreview={null}
        compressing={true}
        onPhotoChange={vi.fn()}
      />
    );
    expect(screen.getByText('Processando...')).toBeDefined();
  });

  it('should render preview image when photoPreview is provided', () => {
    render(
      <UploadButton
        photoPreview="http://example.com/pet.jpg"
        compressing={false}
        onPhotoChange={vi.fn()}
      />
    );
    const img = screen.getByAltText('Preview do Pet') as HTMLImageElement;
    expect(img).toBeDefined();
    expect(img.src).toBe('http://example.com/pet.jpg');
  });

  it('should trigger onPhotoChange when a file is selected', () => {
    const handlePhotoChange = vi.fn();
    render(
      <UploadButton
        photoPreview={null}
        compressing={false}
        onPhotoChange={handlePhotoChange}
      />
    );
    
    const file = new File(['dummy content'], 'test.png', { type: 'image/png' });
    const input = screen.getByTestId('input-foto-pet');
    
    fireEvent.change(input, { target: { files: [file] } });
    expect(handlePhotoChange).toHaveBeenCalled();
  });

  it('should click input when preview area is clicked', () => {
    render(
      <UploadButton
        photoPreview={null}
        compressing={false}
        onPhotoChange={vi.fn()}
      />
    );

    const input = screen.getByTestId('input-foto-pet');
    const clickSpy = vi.spyOn(input, 'click');

    const previewArea = screen.getByLabelText('Selecionar foto do pet');
    fireEvent.click(previewArea);

    expect(clickSpy).toHaveBeenCalled();
  });

  it('should display error message if provided', () => {
    render(
      <UploadButton
        photoPreview={null}
        compressing={false}
        error="Erro ao fazer upload"
        onPhotoChange={vi.fn()}
      />
    );
    expect(screen.getByText('Erro ao fazer upload')).toBeDefined();
  });
});
