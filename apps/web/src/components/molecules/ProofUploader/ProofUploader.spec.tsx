import { render, screen, fireEvent } from '@testing-library/react';
import { ProofUploader } from './index';
import { vi, describe, it, expect } from 'vitest';
import React from 'react';

describe('ProofUploader Component', () => {
  it('should render placeholder when no proof preview is provided', () => {
    render(
      <ProofUploader
        proofPreview={null}
        loading={false}
        onFileChange={vi.fn()}
      />
    );
    expect(screen.getByText('Anexar Comprovante')).toBeDefined();
  });

  it('should render loading state when uploading', () => {
    render(
      <ProofUploader
        proofPreview={null}
        loading={true}
        onFileChange={vi.fn()}
      />
    );
    expect(screen.getByText('Enviando...')).toBeDefined();
  });

  it('should render preview image when proofPreview is provided', () => {
    render(
      <ProofUploader
        proofPreview="http://example.com/proof.jpg"
        loading={false}
        onFileChange={vi.fn()}
      />
    );
    const img = screen.getByAltText('Preview do Comprovante') as HTMLImageElement;
    expect(img).toBeDefined();
    expect(img.src).toBe('http://example.com/proof.jpg');
  });

  it('should trigger onFileChange when a file is selected', () => {
    const handleFileChange = vi.fn();
    render(
      <ProofUploader
        proofPreview={null}
        loading={false}
        onFileChange={handleFileChange}
      />
    );
    
    const file = new File(['dummy content'], 'proof.pdf', { type: 'application/pdf' });
    const input = screen.getByTestId('input-comprovante');
    
    fireEvent.change(input, { target: { files: [file] } });
    expect(handleFileChange).toHaveBeenCalled();
  });

  it('should click input when preview area is clicked', () => {
    render(
      <ProofUploader
        proofPreview={null}
        loading={false}
        onFileChange={vi.fn()}
      />
    );

    const input = screen.getByTestId('input-comprovante');
    const clickSpy = vi.spyOn(input, 'click');

    const previewArea = screen.getByLabelText('Selecionar comprovante');
    fireEvent.click(previewArea);

    expect(clickSpy).toHaveBeenCalled();
  });

  it('should display error message if provided', () => {
    render(
      <ProofUploader
        proofPreview={null}
        loading={false}
        error="Erro ao anexar arquivo"
        onFileChange={vi.fn()}
      />
    );
    expect(screen.getByText('Erro ao anexar arquivo')).toBeDefined();
  });
});
