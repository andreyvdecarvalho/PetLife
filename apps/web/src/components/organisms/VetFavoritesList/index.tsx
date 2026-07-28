import React, { useEffect } from 'react';
import { useVetFavorites } from '../../../application/veterinarian/useVetFavorites';
import { VetCard } from '../../molecules/VetCard';
import './styles.css';

export function VetFavoritesList() {
  const { listFavorites, removeFavorite, favorites, loading, error } = useVetFavorites();

  useEffect(() => {
    listFavorites();
  }, [listFavorites]);

  const handleRemove = async (e: React.MouseEvent, id: string) => {
    e.preventDefault();
    await removeFavorite(id);
  };

  if (error) {
    return <p className="organism-vet-favorites-list__error">{error}</p>;
  }

  if (loading) {
    return <p>Carregando...</p>;
  }

  if (favorites.length === 0) {
    return <p>Você ainda não possui veterinários favoritos.</p>;
  }

  return (
    <div className="organism-vet-favorites-list">
      {favorites.map((vet) => (
        <div key={vet.id} className="organism-vet-favorites-list__item">
          <VetCard veterinarian={vet} />
          <button 
            className="organism-vet-favorites-list__remove-btn" 
            onClick={(e) => handleRemove(e, vet.id)}
          >
            Remover Favorito
          </button>
        </div>
      ))}
    </div>
  );
}
