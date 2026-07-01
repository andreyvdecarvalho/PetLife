import React from 'react';
import { AuthLayout } from '../components/templates/AuthLayout';
import { RegisterForm } from '../components/organisms/RegisterForm';

export const RegisterPage: React.FC = () => {
  return (
    <AuthLayout>
      <RegisterForm />
    </AuthLayout>
  );
};
