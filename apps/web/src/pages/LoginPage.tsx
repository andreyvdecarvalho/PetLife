import React from 'react';
import { AuthLayout } from '../components/templates/AuthLayout/AuthLayout';
import { LoginForm } from '../components/organisms/LoginForm/LoginForm';

export const LoginPage: React.FC = () => {
  return (
    <AuthLayout>
      <LoginForm />
    </AuthLayout>
  );
};
