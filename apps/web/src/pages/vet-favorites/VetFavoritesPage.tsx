import React from 'react';
import { VetFavoritesList } from '../../components/organisms/VetFavoritesList';
import './VetFavoritesPage.css';

export function VetFavoritesPage() {
  return (
    <div className="vet-favorites-page">
      <h1 className="vet-favorites-page__title">Meus Veterinários Favoritos</h1>
      <VetFavoritesList />
    </div>
  );
}
