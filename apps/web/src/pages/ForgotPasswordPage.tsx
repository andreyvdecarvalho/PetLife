import React from 'react';
import { AuthLayout } from '../components/templates/AuthLayout/AuthLayout';
import { ForgotPasswordForm } from '../components/organisms/ForgotPasswordForm/ForgotPasswordForm';

export const ForgotPasswordPage: React.FC = () => {
  return (
    <AuthLayout>
      <ForgotPasswordForm />
    </AuthLayout>
  );
};
