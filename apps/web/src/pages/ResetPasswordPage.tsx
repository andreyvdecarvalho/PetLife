import React from 'react';
import { AuthLayout } from '../components/templates/AuthLayout';
import { ResetPasswordForm } from '../components/organisms/ResetPasswordForm';

export const ResetPasswordPage: React.FC = () => {
  return (
    <AuthLayout>
      <ResetPasswordForm />
    </AuthLayout>
  );
};
