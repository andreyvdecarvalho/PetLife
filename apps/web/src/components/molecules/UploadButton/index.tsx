import React, { useRef } from 'react';
import './styles.css';

interface UploadButtonProps {
  photoPreview: string | null;
  compressing: boolean;
  error?: string;
  onPhotoChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export const UploadButton: React.FC<UploadButtonProps> = ({ 
  photoPreview, 
  compressing, 
  error, 
  onPhotoChange 
}) => {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleSelectPhotoClick = () => {
    fileInputRef.current?.click();
  };

  return (
    <div className="molecule-upload-button">
      <div 
        className="molecule-upload-button__preview" 
        onClick={handleSelectPhotoClick}
        role="button"
        tabIndex={0}
        aria-label="Selecionar foto do pet"
      >
        {photoPreview ? (
          <img src={photoPreview} alt="Preview do Pet" className="molecule-upload-button__img" />
        ) : (
          <div className="molecule-upload-button__placeholder">
            <span className="material-symbols-outlined">pets</span>
            <span>{compressing ? 'Processando...' : 'Adicionar Foto'}</span>
          </div>
        )}
      </div>
      <input 
        type="file" 
        ref={fileInputRef} 
        onChange={onPhotoChange} 
        accept="image/*"
        style={{ display: 'none' }}
        data-testid="input-foto-pet"
      />
      {error && <span className="molecule-upload-button__error">{error}</span>}
    </div>
  );
};
