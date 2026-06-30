/**
 * Utilitários de validação centralizados.
 * Princípio: DRY — evita duplicação de regex em LoginForm, RegisterForm,
 * ProfileForm, ForgotPasswordForm, ResetPasswordForm.
 */

/** Valida formato básico de e-mail */
export const isValidEmail = (email: string): boolean =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

/**
 * Valida força da senha:
 * - Mínimo 8 caracteres
 * - Ao menos 1 letra maiúscula
 * - Ao menos 1 dígito
 */
export const isValidPassword = (password: string): boolean =>
  /^(?=.*[A-Z])(?=.*\d).{8,}$/.test(password);

/** Valida se string não está vazia após trim */
export const isNotEmpty = (value: string): boolean =>
  value.trim().length > 0;

/** Valida comprimento mínimo */
export const hasMinLength = (value: string, min: number): boolean =>
  value.trim().length >= min;
