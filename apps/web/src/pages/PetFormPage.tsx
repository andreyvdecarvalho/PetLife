import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { PetFormPage as PetFormPageContent } from '../components/pages/PetFormPage';

export const PetFormPage: React.FC = () => {
  return (
    <DashboardLayout>
      <PetFormPageContent />
    </DashboardLayout>
  );
};
