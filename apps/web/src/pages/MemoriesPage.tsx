import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { MemoriesPage as MemoriesPageContent } from '../components/pages/MemoriesPage';

export const MemoriesPage: React.FC = () => {
  return (
    <DashboardLayout>
      <MemoriesPageContent />
    </DashboardLayout>
  );
};
