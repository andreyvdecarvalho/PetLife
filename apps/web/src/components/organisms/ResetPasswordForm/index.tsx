import React, { useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { FormField } from '../../molecules/FormField';
import { Button } from '../../atoms/Button';
import { authApi } from '../../../infrastructure/http/auth.api';
import { useToast } from '../../molecules/Toast';
import { isValidPassword } from '../../../utils/validators';
import './styles.css';

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
    } else if (!isValidPassword(password)) {
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
      await authApi.resetPassword(token, password);
      showToast('Senha redefinida com sucesso! ✨', 'success');
      navigate('/login');
    } catch (err: any) {
      console.error(err);
      const message = err.response?.data?.error?.message || 'Token inválido ou expirado.';
      showToast(message, 'error');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form className="organism-reset-password" onSubmit={handleSubmit} data-testid="reset-password-form" noValidate>
      <h2 className="organism-reset-password-title">Nova Senha</h2>
      <p className="organism-reset-password-desc">
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

      <div className="organism-reset-password-footer">
        <Link to="/login" className="organism-reset-password-link">
          Voltar para o Login
        </Link>
      </div>
    </form>
  );
};
