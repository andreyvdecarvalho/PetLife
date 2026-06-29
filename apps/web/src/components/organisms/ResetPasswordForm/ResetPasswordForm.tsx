import React, { useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { FormField } from '../../molecules/FormField/FormField';
import { Button } from '../../atoms/Button/Button';
import api from '../../../services/api';
import { useToast } from '../../molecules/Toast/Toast';
import '../LoginForm/LoginForm.css';

export const ResetPasswordForm: React.FC = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token') || '';
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const validate = () => {
    if (!password) {
      setError('A nova senha é obrigatória.');
      return false;
    } else if (!/^(?=.*[A-Z])(?=.*\d).{8,}$/.test(password)) {
      setError('A senha deve ter no mínimo 8 caracteres, com pelo menos uma letra maiúscula e um número.');
      return false;
    }
    setError('');
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!token) {
      showToast('Token de redefinição ausente. Solicite um novo link.', 'error');
      return;
    }

    if (!validate()) return;

    setIsLoading(true);
    try {
      await api.post('/auth/reset-password', { token, newPassword: password });
      showToast('Senha redefinida com sucesso! ✨', 'success');
      navigate('/login');
    } catch (error: any) {
      console.error(error);
      const message = error.response?.data?.error?.message || 'Token inválido ou expirado.';
      showToast(message, 'error');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form className="auth-form" onSubmit={handleSubmit} data-testid="reset-password-form" noValidate>
      <h2 className="auth-form-title">Nova Senha</h2>
      <p style={{ color: 'var(--text-secondary)', marginBottom: '24px', fontSize: '14px', lineHeight: '1.5' }}>
        Escolha uma nova senha forte contendo pelo menos 8 caracteres, 1 letra maiúscula e 1 número.
      </p>

      <FormField
        label="Nova Senha"
        type="password"
        id="password"
        placeholder="Mínimo 8 caracteres, 1 maiúscula, 1 número"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        error={error}
        required
      />

      <Button type="submit" isLoading={isLoading} disabled={!token}>
        Alterar Senha
      </Button>

      <div className="auth-form-footer">
        <Link to="/login" className="auth-link">
          Voltar para o Login
        </Link>
      </div>
    </form>
  );
};
