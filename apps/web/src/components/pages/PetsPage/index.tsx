import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useGetPets } from '../../../application/pet/useGetPets';
import { useDeletePet } from '../../../application/pet/useDeletePet';
import { PetCard } from '../../molecules/PetCard';
import { Button } from '../../atoms/Button';
import { useToast } from '../../molecules/Toast';
import './styles.css';

export const PetsPageContent: React.FC = () => {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const { pets, isLoading, fetchPets } = useGetPets();
  const { deletePet, loading: isDeleting } = useDeletePet();
  const [deletingId, setDeletingId] = useState<string | null>(null);

  useEffect(() => {
    fetchPets();
  }, [fetchPets]);

  const handleEdit = (pet: any) => {
    navigate('/pets/new', { state: { pet } });
  };

  const handleDelete = async (petId: string) => {
    if (!window.confirm('Tem certeza que deseja excluir este pet? Esta ação não pode ser desfeita.')) return;
    
    setDeletingId(petId);
    try {
      await deletePet(petId);
      showToast('Pet excluído com sucesso.', 'success');
      fetchPets();
    } catch (err) {
      showToast('Erro ao excluir pet.', 'error');
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <div className="pets-page animate-fade-in">
      <div className="pets-page__header">
        <div>
          <h1 className="pets-page__title">Meus Pets</h1>
          <p className="pets-page__subtitle">Gerencie seus pets cadastrados.</p>
        </div>
        <Button onClick={() => navigate('/pets/new')} className="pets-page__add-btn">
          <span className="material-symbols-outlined">add</span>
          Novo Pet
        </Button>
      </div>

      <div className="pets-page__content">
        {isLoading && <p className="pets-page__loading">Carregando pets...</p>}
        
        {!isLoading && pets.length === 0 && (
          <p className="pets-page__empty">Você ainda não tem nenhum pet cadastrado.</p>
        )}

        {!isLoading && pets.length > 0 && (
          <div className="pets-page__grid">
            {pets.map((pet) => (
              <div key={pet.id} className="pets-page__card-wrapper">
                <PetCard
                  pet={pet}
                  isActive={false}
                  onClick={() => handleEdit(pet)}
                />
                <div className="pets-page__card-actions">
                  <Button 
                    variant="outline" 
                    onClick={() => handleEdit(pet)}
                    className="pets-page__action-btn"
                  >
                    <span className="material-symbols-outlined">edit</span>
                    Editar
                  </Button>
                  <Button 
                    variant="danger" 
                    onClick={() => handleDelete(pet.id)}
                    disabled={isDeleting && deletingId === pet.id}
                    className="pets-page__action-btn"
                  >
                    <span className="material-symbols-outlined">delete</span>
                    Excluir
                  </Button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
