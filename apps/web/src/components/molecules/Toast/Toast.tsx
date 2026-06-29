import React, { createContext, useContext, useState, useCallback } from 'react';
import './Toast.css';

type ToastType = 'success' | 'error';

interface ToastMessage {
  id: string;
  type: ToastType;
  message: string;
}

interface ToastContextData {
  showToast: (message: string, type: ToastType) => void;
}

const ToastContext = createContext<ToastContextData>({} as ToastContextData);

export const ToastProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const showToast = useCallback((message: string, type: ToastType) => {
    const id = UUID();
    setToasts((prev) => [...prev, { id, type, message }]);

    // Remove automaticamente após 3 segundos
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 3000);
  }, []);

  // Função auxiliar para gerar ID único
  const UUID = () => {
    return Math.random().toString(36).substring(2, 9);
  };

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <div className="toast-container" data-testid="toast-container">
        {toasts.map((toast) => (
          <div
            key={toast.id}
            className={`toast-item toast-${toast.type}`}
            data-testid={`toast-${toast.type}`}
          >
            <span className="toast-icon">
              {toast.type === 'success' ? '✨' : '❌'}
            </span>
            <span className="toast-message">{toast.message}</span>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast deve ser usado com um ToastProvider');
  }
  return context;
};
