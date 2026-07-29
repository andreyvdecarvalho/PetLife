import React from 'react';
import { render, screen, fireEvent, createEvent } from '@testing-library/react';
import { vi } from 'vitest';
import { AttachmentManager } from './index';

describe('AttachmentManager', () => {
  const mockOnFilesChange = vi.fn();
  const mockOnDeleteExisting = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    window.URL.createObjectURL = vi.fn(() => 'blob:mock-url');
  });

  it('renders correctly with no files', () => {
    render(
      <AttachmentManager 
        files={[]} 
        onFilesChange={mockOnFilesChange} 
      />
    );
    expect(screen.getByText(/Anexos/i)).toBeInTheDocument();
    expect(screen.getByText(/Arraste e solte/i)).toBeInTheDocument();
  });

  it('renders existing attachments', () => {
    render(
      <AttachmentManager 
        files={[]} 
        existingAttachments={['http://example.com/file1.pdf', 'http://example.com/image.png']}
        onFilesChange={mockOnFilesChange}
        onDeleteExisting={mockOnDeleteExisting}
      />
    );
    
    expect(screen.getByText('file1.pdf')).toBeInTheDocument();
    expect(screen.getByText('image.png')).toBeInTheDocument();
    
    // Check if image thumbnail is rendered
    expect(screen.getByAltText('image.png')).toBeInTheDocument();
    
    // Test delete existing
    const removeBtns = screen.getAllByRole('button', { name: /Remover/i });
    expect(removeBtns.length).toBe(2);
    fireEvent.click(removeBtns[0]);
    expect(mockOnDeleteExisting).toHaveBeenCalledWith(0);
  });

  it('handles valid file selection via input', () => {
    render(<AttachmentManager files={[]} onFilesChange={mockOnFilesChange} />);
    
    const file = new File(['hello'], 'hello.png', { type: 'image/png' });
    const input = screen.getByTestId('attachment-input') as HTMLInputElement;
    // Wait, there is no test ID, I must select by type
    const inputEl = document.querySelector('input[type="file"]') as HTMLInputElement;
    
    fireEvent.change(inputEl, { target: { files: [file] } });
    
    expect(mockOnFilesChange).toHaveBeenCalledWith([file]);
  });

  it('shows error when file is too large', () => {
    render(<AttachmentManager files={[]} onFilesChange={mockOnFilesChange} maxSizeMB={1} />);
    
    // Create a 2MB file
    const largeFile = new File([new ArrayBuffer(2 * 1024 * 1024 + 1)], 'large.pdf', { type: 'application/pdf' });
    const inputEl = document.querySelector('input[type="file"]') as HTMLInputElement;
    
    fireEvent.change(inputEl, { target: { files: [largeFile] } });
    
    expect(mockOnFilesChange).not.toHaveBeenCalled();
    expect(screen.getByText(/excede o limite de 1MB/i)).toBeInTheDocument();
  });

  it('shows error when file type is invalid', () => {
    render(<AttachmentManager files={[]} onFilesChange={mockOnFilesChange} />);
    
    const invalidFile = new File(['hello'], 'hello.txt', { type: 'text/plain' });
    const inputEl = document.querySelector('input[type="file"]') as HTMLInputElement;
    
    fireEvent.change(inputEl, { target: { files: [invalidFile] } });
    
    expect(mockOnFilesChange).not.toHaveBeenCalled();
    expect(screen.getByText(/não é permitido/i)).toBeInTheDocument();
  });

  it('shows error when exceeding max files', () => {
    render(
      <AttachmentManager 
        files={[new File([''], '1.png')]} 
        existingAttachments={['2.png']}
        onFilesChange={mockOnFilesChange} 
        maxFiles={2}
      />
    );
    
    const extraFile = new File([''], '3.png', { type: 'image/png' });
    const inputEl = document.querySelector('input[type="file"]') as HTMLInputElement;
    
    fireEvent.change(inputEl, { target: { files: [extraFile] } });
    
    expect(mockOnFilesChange).not.toHaveBeenCalled();
    expect(screen.getByText(/Limite máximo de 2 arquivos excedido/i)).toBeInTheDocument();
  });

  it('handles file removal for new files', () => {
    const file = new File([''], 'new.png', { type: 'image/png' });
    render(<AttachmentManager files={[file]} onFilesChange={mockOnFilesChange} />);
    
    const removeBtn = screen.getByRole('button', { name: /Remover new.png/i });
    fireEvent.click(removeBtn);
    
    expect(mockOnFilesChange).toHaveBeenCalledWith([]);
  });
});
