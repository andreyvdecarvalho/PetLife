import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { FormField } from '../../molecules/FormField';
import { Button } from '../../atoms/Button';
import { useAuth } from '../../../contexts/AuthContext';
import { useToast } from '../../molecules/Toast';
import './styles.css';

export const LoginForm: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<{ email?: string; password?: string }>({});
  const [isLoading, setIsLoading] = useState(false);

  const { login, loginWithGoogle } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const handleGoogleLogin = async () => {
    setIsLoading(true);
    try {
      const mockToken = "dummyHeader.eyJlbWFpbCI6Imdvb2dsZS50dXRvckBwZXRsaWZlLmNvbSIsIm5hbWUiOiJHb29nbGUgVHV0b3IiLCJwaWN0dXJlIjoiaHR0cDovL2dvb2dsZS51cmwvYXZhdGFyIn0.dummySignature";
      await loginWithGoogle(mockToken);
      showToast('Autenticado via Google com sucesso! ✨', 'success');
      navigate('/');
    } catch (error: any) {
      console.error(error);
      const message = error.response?.data?.error?.message || 'Falha ao autenticar com o Google.';
      showToast(message, 'error');
    } finally {
      setIsLoading(false);
    }
  };

  const validate = () => {
    const newErrors: { email?: string; password?: string } = {};

    if (!email) {
      newErrors.email = 'O e-mail é obrigatório.';
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = 'Formato de e-mail inválido.';
    }

    if (!password) {
      newErrors.password = 'A senha é obrigatória.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setIsLoading(true);
    try {
      await login(email, password);
      showToast('Login realizado com sucesso! ✨', 'success');
      navigate('/');
    } catch (error: any) {
      console.error(error);
      const message = error.response?.data?.error?.message || 'E-mail ou senha incorretos.';
      showToast(message, 'error');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form className="auth-form" onSubmit={handleSubmit} data-testid="login-form" noValidate>
      <h2 className="auth-form-title">Entrar na Conta</h2>

      <FormField
        label="E-mail"
        type="email"
        id="email"
        placeholder="seu-email@petlife.com"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        error={errors.email}
        required
      />

      <FormField
        label="Senha"
        type="password"
        id="password"
        placeholder="••••••••"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        error={errors.password}
        required
      />

      <Button type="submit" isLoading={isLoading}>
        Entrar
      </Button>

      <div style={{ display: 'flex', alignItems: 'center', margin: '20px 0', color: 'var(--text-muted)' }}>
        <div style={{ flex: 1, height: '1px', background: 'var(--border-color)' }} />
        <span style={{ padding: '0 10px', fontSize: '14px' }}>ou</span>
        <div style={{ flex: 1, height: '1px', background: 'var(--border-color)' }} />
      </div>

      <Button type="button" variant="google" onClick={handleGoogleLogin} isLoading={isLoading} style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
        <span style={{ fontSize: '18px', fontWeight: 'bold' }}>G</span> Entrar com o Google
      </Button>

      <div className="auth-form-footer">
        <Link to="/forgot-password" className="auth-link">
          Esqueceu a senha?
        </Link>
        <span>
          Não tem uma conta?{' '}
          <Link to="/register" className="auth-link">
            Cadastre-se
          </Link>
        </span>
      </div>
    </form>
  );
};
