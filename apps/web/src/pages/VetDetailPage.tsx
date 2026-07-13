import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { VetDetailPage as VetDetailPageContent } from '../components/pages/VetDetailPage';

export const VetDetailPage: React.FC = () => {
  return (
    <DashboardLayout>
      <VetDetailPageContent />
    </DashboardLayout>
  );
};
