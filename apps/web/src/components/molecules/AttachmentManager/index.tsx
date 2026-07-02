import React, { useRef, useState } from 'react';
import './styles.css';

interface AttachmentManagerProps {
  files: File[];
  onFilesChange: (files: File[]) => void;
  existingAttachments?: string[];
  onDeleteExisting?: (index: number) => void;
  maxFiles?: number;
  maxSizeMB?: number;
}

export const AttachmentManager: React.FC<AttachmentManagerProps> = ({
  files,
  onFilesChange,
  existingAttachments = [],
  onDeleteExisting,
  maxFiles = 5,
  maxSizeMB = 2,
}) => {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [dragActive, setDragActive] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const totalFilesCount = files.length + existingAttachments.length;

  const validateAndAddFiles = (selectedFiles: FileList) => {
    setError(null);
    const newFiles = Array.from(selectedFiles);

    if (totalFilesCount + newFiles.length > maxFiles) {
      setError(`Limite máximo de ${maxFiles} arquivos excedido.`);
      return;
    }

    const validFiles: File[] = [];
    for (const file of newFiles) {
      if (file.size > maxSizeMB * 1024 * 1024) {
        setError(`O arquivo ${file.name} excede o limite de ${maxSizeMB}MB.`);
        return;
      }

      const lowerName = file.name.toLowerCase();
      if (!lowerName.endsWith('.pdf') && !lowerName.endsWith('.png') && !lowerName.endsWith('.jpeg') && !lowerName.endsWith('.jpg')) {
        setError(`O tipo do arquivo ${file.name} não é permitido. Apenas PDF, PNG e JPEG são aceitos.`);
        return;
      }
      validFiles.push(file);
    }

    onFilesChange([...files, ...validFiles]);
  };

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      validateAndAddFiles(e.dataTransfer.files);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    e.preventDefault();
    if (e.target.files && e.target.files[0]) {
      validateAndAddFiles(e.target.files);
    }
  };

  const removeNewFile = (index: number) => {
    const updated = files.filter((_, i) => i !== index);
    onFilesChange(updated);
  };

  const getFileName = (url: string) => {
    return url.substring(url.lastIndexOf('/') + 1);
  };

  const isImage = (fileName: string) => {
    const lower = fileName.toLowerCase();
    return lower.endsWith('.png') || lower.endsWith('.jpg') || lower.endsWith('.jpeg');
  };

  return (
    <div className="molecule-attachment-manager">
      <label className="molecule-attachment-manager__label">Anexos (Máx. 5 arquivos - JPEG, PNG ou PDF de até 2MB)</label>
      
      {/* Drag & Drop Area */}
      <div 
        className={`molecule-attachment-manager__dropzone ${dragActive ? 'active' : ''}`}
        onDragEnter={handleDrag}
        onDragOver={handleDrag}
        onDragLeave={handleDrag}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current?.click()}
      >
        <input 
          ref={fileInputRef}
          type="file"
          multiple
          accept=".pdf,.png,.jpeg,.jpg"
          className="molecule-attachment-manager__input"
          onChange={handleChange}
        />
        <span className="material-symbols-outlined molecule-attachment-manager__upload-icon">upload_file</span>
        <p className="molecule-attachment-manager__dropzone-text">
          Arraste e solte seus arquivos aqui ou <span>clique para buscar</span>
        </p>
      </div>

      {error && <span className="molecule-attachment-manager__error">{error}</span>}

      {/* Attachments List */}
      {totalFilesCount > 0 && (
        <div className="molecule-attachment-manager__list">
          {/* Existing (Already Uploaded) */}
          {existingAttachments.map((url, index) => {
            const fileName = getFileName(url);
            return (
              <div key={`existing-${index}`} className="molecule-attachment-manager__item">
                {isImage(fileName) ? (
                  <img src={url} alt={fileName} className="molecule-attachment-manager__thumbnail" />
                ) : (
                  <div className="molecule-attachment-manager__pdf-icon">
                    <span className="material-symbols-outlined">picture_as_pdf</span>
                  </div>
                )}
                <span className="molecule-attachment-manager__filename" title={fileName}>{fileName}</span>
                {onDeleteExisting && (
                  <button 
                    type="button" 
                    className="molecule-attachment-manager__remove-btn"
                    onClick={() => onDeleteExisting(index)}
                    aria-label={`Remover ${fileName}`}
                  >
                    <span className="material-symbols-outlined">close</span>
                  </button>
                )}
              </div>
            );
          })}

          {/* New Files (To be Uploaded) */}
          {files.map((file, index) => {
            const isImg = isImage(file.name);
            return (
              <div key={`new-${index}`} className="molecule-attachment-manager__item new">
                {isImg ? (
                  <img src={URL.createObjectURL(file)} alt={file.name} className="molecule-attachment-manager__thumbnail" />
                ) : (
                  <div className="molecule-attachment-manager__pdf-icon">
                    <span className="material-symbols-outlined">picture_as_pdf</span>
                  </div>
                )}
                <span className="molecule-attachment-manager__filename" title={file.name}>{file.name} (Novo)</span>
                <button 
                  type="button" 
                  className="molecule-attachment-manager__remove-btn"
                  onClick={() => removeNewFile(index)}
                  aria-label={`Remover ${file.name}`}
                >
                  <span className="material-symbols-outlined">close</span>
                </button>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
