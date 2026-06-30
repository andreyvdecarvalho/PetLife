import React from 'react';
import { DashboardLayout } from '../components/templates/DashboardLayout';
import { RoutinePage as RoutinePageContent } from '../components/pages/RoutinePage';

export const RoutinePage: React.FC = () => {
  return (
    <DashboardLayout>
      <RoutinePageContent />
    </DashboardLayout>
  );
};
