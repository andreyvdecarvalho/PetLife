import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { GroomingPageContent } from '../components/pages/GroomingPage';

export const GroomingPage: React.FC = () => {
  return (
    <DashboardLayout>
      <GroomingPageContent />
    </DashboardLayout>
  );
};

export default GroomingPage;
