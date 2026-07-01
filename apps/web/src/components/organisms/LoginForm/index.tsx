import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { FormField } from '../../molecules/FormField';
import { Button } from '../../atoms/Button';
import { useAuth } from '../../../contexts/AuthContext';
import { useToast } from '../../molecules/Toast';
import { isValidEmail } from '../../../utils/validators';
import './styles.css';

export const LoginForm: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [errors, setErrors] = useState<{ email?: string; password?: string }>({});
  const [isLoading, setIsLoading] = useState(false);

  const { login } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const handleGoogleLogin = async () => {
    // TODO: Integrar Google OAuth SDK real (tarefa separada)
    // Nunca usar tokens mock em código de produção
    console.warn('[PetLife] Login com Google ainda não implementado. Integração com SDK real pendente.');
  };

  const validate = () => {
    const newErrors: { email?: string; password?: string } = {};

    if (!email) {
      newErrors.email = 'O e-mail é obrigatório.';
    } else if (!isValidEmail(email)) {
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
    <div className="organism-login-container">
      {/* Brand Illustration / Logo Area */}
      <div className="organism-login-header">
        <div className="organism-login-header__icon-wrapper">
          <span className="material-symbols-outlined filled">pets</span>
        </div>
        <h2 className="organism-login-header__title">Bem-vindo de volta!</h2>
        <p className="organism-login-header__subtitle">Acesse a conta do seu melhor amigo.</p>
      </div>

      <div className="organism-login-card-wrapper">
        <div className="organism-login-card">
          {/* Soft ambient decorative element */}
          <div className="organism-login-card__decorative" />

          <form className="organism-login-form relative z-10" onSubmit={handleSubmit} data-testid="login-form" noValidate>
            <FormField
              label="E-mail"
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
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              error={errors.password}
              iconLeft="lock"
              rightElement={
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="organism-login-form__toggle-password"
                >
                  <span className="material-symbols-outlined">
                    {showPassword ? 'visibility' : 'visibility_off'}
                  </span>
                </button>
              }
              required
            />

            <div className="organism-login-form__forgot-password">
              <Link to="/forgot-password">Esqueci minha senha</Link>
            </div>

            <Button type="submit" isLoading={isLoading} className="organism-login-form__submit">
              Entrar
            </Button>
          </form>

          <div className="organism-login-divider relative z-10">
            <div className="organism-login-divider__line" />
            <span className="organism-login-divider__text">Ou continue com</span>
          </div>

          <div className="organism-login-social relative z-10">
            <button
              type="button"
              onClick={handleGoogleLogin}
              className="organism-login-social__btn"
              disabled
              title="Login com Google em implementação"
            >
              <svg aria-hidden="true" className="w-5 h-5" viewBox="0 0 24 24">
                <path d="M12.0003 4.75C13.7703 4.75 15.3553 5.36 16.6053 6.54998L20.0303 3.125C17.9502 1.19 15.2353 0 12.0003 0C7.31028 0 3.25527 2.69 1.28027 6.60998L5.27028 9.70498C6.21525 6.86002 8.87028 4.75 12.0003 4.75Z" fill="#EA4335"></path>
                <path d="M23.49 12.275C23.49 11.49 23.415 10.73 23.3 10H12V14.51H18.47C18.18 15.99 17.34 17.25 16.08 18.1L19.945 21.1C22.2 19.01 23.49 15.92 23.49 12.275Z" fill="#4285F4"></path>
                <path d="M5.26498 14.2949C5.02498 13.5699 4.88501 12.7999 4.88501 11.9999C4.88501 11.1999 5.01998 10.4299 5.26498 9.7049L1.275 6.60986C0.46 8.22986 0 10.0599 0 11.9999C0 13.9399 0.46 15.7699 1.28 17.3899L5.26498 14.2949Z" fill="#FBBC05"></path>
                <path d="M12.0004 24.0001C15.2404 24.0001 17.9654 22.935 19.9454 21.095L16.0804 18.095C15.0054 18.82 13.6204 19.245 12.0004 19.245C8.8704 19.245 6.21537 17.135 5.26538 14.29L1.27539 17.385C3.25539 21.31 7.3104 24.0001 12.0004 24.0001Z" fill="#34A853"></path>
              </svg>
              Google
            </button>
            <button
              type="button"
              className="organism-login-social__btn"
              disabled
              title="Login com Apple em implementação"
            >
              <svg aria-hidden="true" className="w-5 h-5 fill-current" viewBox="0 0 24 24">
                <path d="M16.365 21.44c-1.35.95-2.73 1.44-4.22 1.44-1.55 0-3.04-.52-4.43-1.53-1.63-1.18-2.9-2.77-3.79-4.72-.9-1.95-1.36-4.05-1.36-6.24 0-1.89.44-3.61 1.3-5.07.88-1.52 2.1-2.67 3.63-3.41C8.94 1.21 10.45.85 12.02.85c1.47 0 2.91.43 4.26 1.25.96.59 1.76 1.34 2.37 2.22-.64.55-1.17 1.21-1.57 1.95-.5.91-.76 1.91-.76 2.95 0 1.29.35 2.49 1.02 3.55.67 1.05 1.61 1.83 2.76 2.31-.38 1.48-.94 2.87-1.68 4.14-.99 1.69-2.01 3.42-3.05 5.17l.02.01zM11.97 21.6c1.19 0 2.38-.45 3.51-1.31 1.04-.8 1.86-1.85 2.42-3.11-.97-.5-1.74-1.22-2.26-2.12-.55-.95-.84-2.02-.84-3.15 0-.96.22-1.87.65-2.68.41-.77.98-1.42 1.66-1.92-1.12-.76-2.35-1.16-3.66-1.16-1.4 0-2.75.4-4.01 1.18-1.4.87-2.48 2.08-3.19 3.58-.72 1.54-1.1 3.26-1.1 5.09 0 1.96.44 3.82 1.28 5.51.8 1.61 1.88 2.95 3.19 3.96 1.16.89 2.38 1.35 3.63 1.35l-1.28-5.22zM15.53 5.4c.54-.62 1.04-1.32 1.48-2.08.35-.61.64-1.29.84-2.01-.84.09-1.65.31-2.43.66-.75.33-1.43.8-2.02 1.39-.56.57-1.02 1.23-1.36 1.96-.34.72-.56 1.5-.64 2.32.89-.04 1.76-.28 2.58-.7.76-.39 1.45-.91 2.05-1.54h-5z"></path>
              </svg>
              Apple
            </button>
          </div>

          <div className="organism-login-footer relative z-10">
            <p>
              Ainda não tem uma conta?
              <Link to="/register">Cadastre-se</Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
