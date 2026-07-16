import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { PetForm } from '../../organisms/PetForm';
import { useToast } from '../../molecules/Toast';
import type { Pet } from '../../../domain/pet/Pet';
import './styles.css';

export const PetFormPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { showToast } = useToast();
  
  const editingPet = location.state?.pet as Pet | undefined;

  const handleSuccess = () => {
    showToast(editingPet ? 'Pet atualizado com sucesso! ✨' : 'Pet cadastrado com sucesso! ✨', 'success');
    navigate('/');
  };

  const handleCancel = () => {
    navigate('/');
  };

  return (
    <div className="pet-form-page animate-fade-in">
      <div className="pet-form-page__header">
        <button 
          className="pet-form-page__back-btn" 
          onClick={handleCancel}
          aria-label="Voltar"
        >
          <span className="material-symbols-outlined">arrow_back</span>
        </button>
        <h1 className="pet-form-page__title">
          {editingPet ? 'Editar Pet' : 'Cadastrar Novo Pet'}
        </h1>
      </div>
      <div className="pet-form-page__content">
        <PetForm 
          pet={editingPet}
          onSuccess={handleSuccess}
          onCancel={handleCancel}
        />
      </div>
    </div>
  );
};
