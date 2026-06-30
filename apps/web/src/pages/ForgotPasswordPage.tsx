import React from 'react';
import { AuthLayout } from '../components/templates/AuthLayout';
import { ForgotPasswordForm } from '../components/organisms/ForgotPasswordForm';

export const ForgotPasswordPage: React.FC = () => {
  return (
    <AuthLayout>
      <ForgotPasswordForm />
    </AuthLayout>
  );
};
