import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { FormField } from '../../molecules/FormField/FormField';
import { Button } from '../../atoms/Button/Button';
import api from '../../../services/api';
import { useToast } from '../../molecules/Toast/Toast';
import '../LoginForm/LoginForm.css';

export const ForgotPasswordForm: React.FC = () => {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  const { showToast } = useToast();

  const validate = () => {
    if (!email) {
      setError('O e-mail é obrigatório.');
      return false;
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      setError('Formato de e-mail inválido.');
      return false;
    }
    setError('');
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setIsLoading(true);
    try {
      await api.post('/auth/forgot-password', { email });
      setIsSuccess(true);
      showToast('Solicitação processada com sucesso! ✨', 'success');
    } catch (error: any) {
      console.error(error);
      showToast('Ocorreu um erro ao processar. Tente novamente.', 'error');
    } finally {
      setIsLoading(false);
    }
  };

  if (isSuccess) {
    return (
      <div className="auth-form" style={{ textAlign: 'center' }}>
        <h2 className="auth-form-title">E-mail Enviado!</h2>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '24px', fontSize: '15px', lineHeight: '1.6' }}>
          Se o e-mail <strong>{email}</strong> estiver cadastrado em nossa plataforma, você receberá instruções para redefinir sua senha em instantes.
        </p>
        <Link to="/login" className="btn-premium btn-secondary" style={{ textDecoration: 'none' }}>
          Voltar para o Login
        </Link>
      </div>
    );
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit} data-testid="forgot-password-form" noValidate>
      <h2 className="auth-form-title">Recuperar Senha</h2>
      <p style={{ color: 'var(--text-secondary)', marginBottom: '24px', fontSize: '14px', lineHeight: '1.5' }}>
        Insira seu e-mail de cadastro. Enviaremos um link de uso único para redefinição da sua senha.
      </p>

      <FormField
        label="E-mail de Cadastro"
        type="email"
        id="email"
        placeholder="seu-email@petlife.com"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        error={error}
        required
      />

      <Button type="submit" isLoading={isLoading}>
        Enviar Link
      </Button>

      <div className="auth-form-footer">
        <span>
          Lembrou a senha?{' '}
          <Link to="/login" className="auth-link">
            Entrar
          </Link>
        </span>
      </div>
    </form>
  );
};
