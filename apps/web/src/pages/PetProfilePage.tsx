import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { PetProfilePage as PetProfilePageContent } from '../components/pages/PetProfilePage';

export const PetProfilePage: React.FC = () => {
  return (
    <DashboardLayout>
      <PetProfilePageContent />
    </DashboardLayout>
  );
};
