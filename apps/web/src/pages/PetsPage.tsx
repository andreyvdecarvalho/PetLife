import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { PetsPageContent } from '../components/pages/PetsPage';

export const PetsPage: React.FC = () => {
  return (
    <DashboardLayout>
      <PetsPageContent />
    </DashboardLayout>
  );
};
