import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { VetSearchPage as VetSearchPageContent } from '../components/pages/VetSearchPage';

export const VetSearchPage: React.FC = () => {
  return (
    <DashboardLayout>
      <VetSearchPageContent />
    </DashboardLayout>
  );
};
