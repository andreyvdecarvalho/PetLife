import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { FormField } from '../../molecules/FormField';
import { Button } from '../../atoms/Button';
import { useAuth } from '../../../contexts/AuthContext';
import { useToast } from '../../molecules/Toast';
import '../LoginForm/styles.css';

export const RegisterForm: React.FC = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<{ name?: string; email?: string; password?: string }>({});
  const [isLoading, setIsLoading] = useState(false);

  const { register, loginWithGoogle } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const handleGoogleLogin = async () => {
    setIsLoading(true);
    try {
      const mockToken = "dummyHeader.eyJlbWFpbCI6Imdvb2dsZS50dXRvckBwZXRsaWZlLmNvbSIsIm5hbWUiOiJHb29nbGUgVHV0b3IiLCJwaWN0dXJlIjoiaHR0cDovL2dvb2dsZS51cmwvYXZhdGFyIn0.dummySignature";
      await loginWithGoogle(mockToken);
      showToast('Conta criada e autenticado via Google! ✨', 'success');
      navigate('/');
    } catch (error: any) {
      console.error(error);
      const message = error.response?.data?.error?.message || 'Falha ao cadastrar com o Google.';
      showToast(message, 'error');
    } finally {
      setIsLoading(false);
    }
  };

  const validate = () => {
    const newErrors: { name?: string; email?: string; password?: string } = {};

    if (!name) {
      newErrors.name = 'O nome é obrigatório.';
    } else if (name.length < 2) {
      newErrors.name = 'O nome deve ter no mínimo 2 caracteres.';
    }

    if (!email) {
      newErrors.email = 'O e-mail é obrigatório.';
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = 'Formato de e-mail inválido.';
    }

    if (!password) {
      newErrors.password = 'A senha é obrigatória.';
    } else if (!/^(?=.*[A-Z])(?=.*\d).{8,}$/.test(password)) {
      newErrors.password = 'A senha deve ter no mínimo 8 caracteres, com pelo menos uma letra maiúscula e um número.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setIsLoading(true);
    try {
      await register(name, email, password);
      showToast('Conta criada com sucesso! Seja bem-vindo(a) ✨', 'success');
      navigate('/');
    } catch (error: any) {
      console.error(error);
      const message = error.response?.data?.error?.message || 'Erro ao criar conta. Tente novamente.';
      showToast(message, 'error');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form className="auth-form" onSubmit={handleSubmit} data-testid="register-form" noValidate>
      <h2 className="auth-form-title">Criar uma Conta</h2>

      <FormField
        label="Nome Completo"
        type="text"
        id="name"
        placeholder="Ex: Camila Silva"
        value={name}
        onChange={(e) => setName(e.target.value)}
        error={errors.name}
        required
      />

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
        placeholder="Mínimo 8 caracteres, 1 maiúscula, 1 número"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        error={errors.password}
        required
      />

      <Button type="submit" isLoading={isLoading}>
        Cadastrar
      </Button>

      <div style={{ display: 'flex', alignItems: 'center', margin: '20px 0', color: 'var(--text-muted)' }}>
        <div style={{ flex: 1, height: '1px', background: 'var(--border-color)' }} />
        <span style={{ padding: '0 10px', fontSize: '14px' }}>ou</span>
        <div style={{ flex: 1, height: '1px', background: 'var(--border-color)' }} />
      </div>

      <Button type="button" variant="google" onClick={handleGoogleLogin} isLoading={isLoading} style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}>
        <span style={{ fontSize: '18px', fontWeight: 'bold' }}>G</span> Cadastrar com o Google
      </Button>

      <div className="auth-form-footer">
        <span>
          Já tem uma conta?{' '}
          <Link to="/login" className="auth-link">
            Entrar
          </Link>
        </span>
      </div>
    </form>
  );
};
