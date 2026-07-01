import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { DashboardPage as DashboardPageContent } from '../components/pages/DashboardPage';

export const DashboardPage: React.FC = () => {
  return (
    <DashboardLayout>
      <DashboardPageContent />
    </DashboardLayout>
  );
};
