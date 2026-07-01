import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { FormField } from '../../molecules/FormField';
import { Button } from '../../atoms/Button';
import { authApi } from '../../../infrastructure/http/auth.api';
import { useToast } from '../../molecules/Toast';
import { isValidEmail } from '../../../utils/validators';
import './styles.css';

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
    } else if (!isValidEmail(email)) {
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
      await authApi.forgotPassword(email);
      setIsSuccess(true);
      showToast('Solicitação processada com sucesso! ✨', 'success');
    } catch (err: any) {
      console.error(err);
      showToast('Ocorreu um erro ao processar. Tente novamente.', 'error');
    } finally {
      setIsLoading(false);
    }
  };

  if (isSuccess) {
    return (
      <div className="organism-forgot-password" style={{ textAlign: 'center' }}>
        <h2 className="organism-forgot-password-title">E-mail Enviado!</h2>
        <p className="organism-forgot-password-desc">
          Se o e-mail <strong>{email}</strong> estiver cadastrado em nossa plataforma, você receberá instruções para redefinir sua senha em instantes.
        </p>
        <Link to="/login" className="organism-forgot-password-back-btn">
          Voltar para o Login
        </Link>
      </div>
    );
  }

  return (
    <form className="organism-forgot-password" onSubmit={handleSubmit} data-testid="forgot-password-form" noValidate>
      <h2 className="organism-forgot-password-title">Recuperar Senha</h2>
      <p className="organism-forgot-password-desc">
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

      <div className="organism-forgot-password-footer">
        <span>
          Lembrou a senha?{' '}
          <Link to="/login" className="organism-forgot-password-link">
            Entrar
          </Link>
        </span>
      </div>
    </form>
  );
};
