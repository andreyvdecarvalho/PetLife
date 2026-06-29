import React from 'react';
import { AuthLayout } from '../components/templates/AuthLayout/AuthLayout';
import { RegisterForm } from '../components/organisms/RegisterForm/RegisterForm';

export const RegisterPage: React.FC = () => {
  return (
    <AuthLayout>
      <RegisterForm />
    </AuthLayout>
  );
};
