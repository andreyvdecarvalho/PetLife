import React from 'react';
import { VetProfileDashboard } from '../../components/organisms/VetProfileDashboard';
import './VetProfilePage.css';

export function VetProfilePage() {
  return (
    <div className="vet-profile-page">
      <h1 className="vet-profile-page__title">Dashboard do Veterinário</h1>
      <VetProfileDashboard />
    </div>
  );
}
