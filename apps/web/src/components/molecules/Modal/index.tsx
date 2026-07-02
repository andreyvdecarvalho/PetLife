import React, { useEffect } from 'react';
import './styles.css';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
}

export const Modal: React.FC<ModalProps> = ({ isOpen, onClose, title, children }) => {
  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    if (isOpen) {
      document.addEventListener('keydown', handleEsc);
      document.body.style.overflow = 'hidden';
    }
    return () => {
      document.removeEventListener('keydown', handleEsc);
      document.body.style.overflow = 'unset';
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <div className="molecule-modal-overlay" onClick={onClose}>
      <div className="molecule-modal-content" onClick={e => e.stopPropagation()}>
        <div className="molecule-modal-header">
          <h3 className="molecule-modal-title">{title}</h3>
          <button className="molecule-modal-close" onClick={onClose} aria-label="Fechar">
            <span className="material-symbols-outlined">close</span>
          </button>
        </div>
        <div className="molecule-modal-body">
          {children}
        </div>
      </div>
    </div>
  );
};
