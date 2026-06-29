import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout/DashboardLayout';
import { useAuth } from '../contexts/AuthContext';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  return (
    <DashboardLayout>
      <div className="animate-fade-in" style={{ textAlign: 'left' }}>
        <h1 style={{ fontSize: '32px', fontWeight: 700, marginBottom: '8px' }}>
          Olá, {user?.name}! 👋
        </h1>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '40px' }}>
          Bem-vindo de volta ao seu painel do PetLife.
        </p>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '24px' }}>
          <div
            style={{
              padding: '24px',
              background: 'var(--bg-card)',
              backdropFilter: 'blur(10px)',
              border: '1px solid var(--border-color)',
              borderRadius: 'var(--radius-lg)',
              boxShadow: 'var(--shadow-premium)'
            }}
          >
            <h3 style={{ fontSize: '18px', fontWeight: 600, marginBottom: '16px' }}>Status da Assinatura</h3>
            <p style={{ fontSize: '15px', color: 'var(--text-secondary)', marginBottom: '8px' }}>
              Plano Ativo: <strong style={{ color: 'var(--primary)' }}>{user?.plan}</strong>
            </p>
            <p style={{ fontSize: '14px', color: 'var(--text-muted)' }}>
              Acesso básico com direito a cadastrar até 2 pets.
            </p>
          </div>

          <div
            style={{
              padding: '24px',
              background: 'var(--bg-card)',
              backdropFilter: 'blur(10px)',
              border: '1px solid var(--border-color)',
              borderRadius: 'var(--radius-lg)',
              boxShadow: 'var(--shadow-premium)'
            }}
          >
            <h3 style={{ fontSize: '18px', fontWeight: 600, marginBottom: '16px' }}>Verificação da Conta</h3>
            <p style={{ fontSize: '15px', color: 'var(--text-secondary)', marginBottom: '8px' }}>
              E-mail: <strong>{user?.email}</strong>
            </p>
            <p style={{ fontSize: '14px', color: user?.emailVerified ? 'var(--success)' : 'var(--text-muted)' }}>
              {user?.emailVerified ? '✨ E-mail verificado com sucesso.' : '⚠️ Por favor, confirme seu e-mail.'}
            </p>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};
