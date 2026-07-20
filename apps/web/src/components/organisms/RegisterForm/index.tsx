import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { FormField } from '../../molecules/FormField';
import { Button } from '../../atoms/Button';
import { useAuth } from '../../../contexts/AuthContext';
import { useToast } from '../../molecules/Toast';
import { isValidEmail, isValidPassword } from '../../../utils/validators';
import './styles.css';

export const RegisterForm: React.FC = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [errors, setErrors] = useState<{ name?: string; email?: string; password?: string; terms?: string }>({});
  const [isLoading, setIsLoading] = useState(false);

  const { register } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const validate = () => {
    const newErrors: { name?: string; email?: string; password?: string; terms?: string } = {};

    if (!name) {
      newErrors.name = 'O nome é obrigatório.';
    } else if (name.length < 2) {
      newErrors.name = 'O nome deve ter no mínimo 2 caracteres.';
    }

    if (!email) {
      newErrors.email = 'O e-mail é obrigatório.';
    } else if (!isValidEmail(email)) {
      newErrors.email = 'Formato de e-mail inválido.';
    }

    if (!password) {
      newErrors.password = 'A senha é obrigatória.';
    } else if (!isValidPassword(password)) {
      newErrors.password = 'A senha deve ter no mínimo 8 caracteres, com pelo menos uma letra maiúscula e um número.';
    }
    
    if (!termsAccepted) {
      newErrors.terms = 'Você precisa aceitar os termos de uso.';
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
      navigate('/onboarding');
    } catch (error: any) {
      console.error(error);
      const message = error.response?.data?.error?.message || 'Erro ao criar conta. Tente novamente.';
      showToast(message, 'error');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="organism-register-container">
      <div className="organism-register-card">
        {/* Header */}
        <div className="organism-register-header">
          <div className="organism-register-header__icon-wrapper">
            <span className="material-symbols-outlined filled">pets</span>
          </div>
          <h1 className="organism-register-header__title">Crie sua conta</h1>
          <p className="organism-register-header__subtitle">Junte-se à nossa comunidade de tutores.</p>
        </div>

        {/* Form */}
        <form className="organism-register-form" onSubmit={handleSubmit} data-testid="register-form" noValidate>
          <FormField
            label="Nome Completo"
            type="text"
            id="fullName"
            placeholder="Seu nome"
            value={name}
            onChange={(e) => setName(e.target.value)}
            error={errors.name}
            iconLeft="person"
            required
          />

          <FormField
            label="Email"
            type="email"
            id="email"
            placeholder="seu@email.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            error={errors.email}
            iconLeft="mail"
            required
          />

          <FormField
            label="Senha"
            type={showPassword ? 'text' : 'password'}
            id="password"
            placeholder="Mínimo 8 caracteres"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            error={errors.password}
            iconLeft="lock"
            rightElement={
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="organism-register-form__toggle-password"
              >
                <span className="material-symbols-outlined">
                  {showPassword ? 'visibility' : 'visibility_off'}
                </span>
              </button>
            }
            required
          />

          {/* Terms */}
          <div className="organism-register-terms">
            <div className="organism-register-terms__checkbox-wrapper">
              <input
                type="checkbox"
                id="terms"
                checked={termsAccepted}
                onChange={(e) => setTermsAccepted(e.target.checked)}
                className="organism-register-terms__checkbox"
              />
            </div>
            <label htmlFor="terms" className="organism-register-terms__label">
              Aceito os <Link to="#">termos e condições</Link> de uso.
            </label>
          </div>
          {errors.terms && <span className="organism-register-form__error-text">{errors.terms}</span>}

          <Button type="submit" isLoading={isLoading} className="organism-register-form__submit">
            Criar Conta
            <span className="material-symbols-outlined">arrow_forward</span>
          </Button>
        </form>

        {/* Login Link */}
        <div className="organism-register-footer">
          <p>
            Já tem uma conta? <Link to="/login">Faça login</Link>
          </p>
        </div>
      </div>
    </div>
  );
};
