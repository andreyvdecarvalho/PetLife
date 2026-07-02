import React, { useRef } from 'react';
import './styles.css';

interface ProofUploaderProps {
  proofPreview: string | null;
  loading: boolean;
  error?: string;
  onFileChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export const ProofUploader: React.FC<ProofUploaderProps> = ({ 
  proofPreview, 
  loading, 
  error, 
  onFileChange 
}) => {
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleSelectFileClick = () => {
    fileInputRef.current?.click();
  };

  return (
    <div className="molecule-proof-uploader">
      <div 
        className="molecule-proof-uploader__preview" 
        onClick={handleSelectFileClick}
        role="button"
        tabIndex={0}
        aria-label="Selecionar comprovante"
      >
        {proofPreview ? (
          <img src={proofPreview} alt="Preview do Comprovante" className="molecule-proof-uploader__img" />
        ) : (
          <div className="molecule-proof-uploader__placeholder">
            <span className="material-symbols-outlined">description</span>
            <span>{loading ? 'Enviando...' : 'Anexar Comprovante'}</span>
          </div>
        )}
      </div>
      <input 
        type="file" 
        ref={fileInputRef} 
        onChange={onFileChange} 
        accept="image/*,application/pdf"
        style={{ display: 'none' }}
        data-testid="input-comprovante"
      />
      {error && <span className="molecule-proof-uploader__error">{error}</span>}
    </div>
  );
};
