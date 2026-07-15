import React, { useEffect } from 'react';
import { useVetFavorites } from '../../application/veterinarian/useVetFavorites';
import { VetCard } from '../../components/molecules/VetCard';
import './VetFavoritesPage.css';

export function VetFavoritesPage() {
  const { listFavorites, removeFavorite, favorites, loading, error } = useVetFavorites();

  useEffect(() => {
    listFavorites();
  }, [listFavorites]);

  const handleRemove = async (e: React.MouseEvent, id: string) => {
    e.preventDefault();
    await removeFavorite(id);
  };

  return (
    <div className="vet-favorites-page">
      <h1 className="vet-favorites-page__title">Meus Veterinários Favoritos</h1>
      {error && <p className="vet-favorites-page__error">{error}</p>}
      {loading ? (
        <p>Carregando...</p>
      ) : favorites.length === 0 ? (
        <p>Você ainda não possui veterinários favoritos.</p>
      ) : (
        <div className="vet-favorites-page__list">
          {favorites.map((vet) => (
            <div key={vet.id} className="vet-favorites-page__item">
              <VetCard veterinarian={vet} />
              <button 
                className="vet-favorites-page__remove-btn" 
                onClick={(e) => handleRemove(e, vet.id)}
              >
                Remover Favorito
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
