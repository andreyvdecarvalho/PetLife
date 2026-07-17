import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FormField } from '../../molecules/FormField';
import { Button } from '../../atoms/Button';
import { useCreatePet } from '../../../application/pet/useCreatePet';
import type { PetSpecies, PetSex } from '../../../domain/pet/Pet';
import './styles.css';

export const OnboardingPageContent: React.FC = () => {
  const navigate = useNavigate();
  const { createPet, loading, error } = useCreatePet();

  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    name: '',
    species: 'DOG' as PetSpecies,
    breed: '',
    sex: 'UNKNOWN' as PetSex,
    birthDate: '',
    weightKg: '',
  });
  const [photo, setPhoto] = useState<File | null>(null);

  const handleNext = () => setStep(s => s + 1);
  const handleBack = () => setStep(s => s - 1);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setPhoto(e.target.files[0]);
    }
  };

  const handleSubmit = async () => {
    try {
      await createPet({
        name: formData.name,
        species: formData.species,
        breed: formData.breed,
        sex: formData.sex,
        birthDate: formData.birthDate || undefined,
        weightKg: formData.weightKg ? Number(formData.weightKg) : undefined,
        neutered: false,
      }, photo || undefined);
      setStep(5);
    } catch (err) {
      console.error(err);
    }
  };

  const renderStep = () => {
    switch (step) {
      case 1:
        return (
          <div className="onboarding-page__step onboarding-page__step--welcome animate-fade-in">
            <main className="onboarding-page__main">
              <div className="onboarding-page__illustration">
                <div className="onboarding-page__gradient-overlay"></div>
                <img 
                  alt="A cheerful and warm photo of a happy dog looking up" 
                  className="onboarding-page__img" 
                  src="https://lh3.googleusercontent.com/aida-public/AB6AXuB5ZoSUBouSGKvObWqbmdYl9jl9EWGYayCnDddGABFlwOeXXZTFPiWE8-hBSnjs-P04txevwdB5y4dGGFA5wEJKc5r_V_mH6RwJrv5cjLYvBo39WMBIJUErJ_PjyEklM5YMFvAp3Wjbg46MW0jGyTpRvsT84lWKACulqXyrDQ70Vk9vQrychqmzzYGEzpsJcb0dt8ah6s_UJJXgS4HraXFRgVEFcdKCrgPVOAPOCusmLrMoDP-8jm18ZBBh6KBBG5QRCjsDgaYckcs" 
                />
                <div className="onboarding-page__floating onboarding-page__floating--favorite">
                  <span className="material-symbols-outlined text-primary filled">favorite</span>
                </div>
                <div className="onboarding-page__floating onboarding-page__floating--pets">
                  <span className="material-symbols-outlined text-on-tertiary-container filled">pets</span>
                </div>
              </div>

              <div className="onboarding-page__text-container">
                <h1 className="onboarding-page__title">Vamos conhecer o seu melhor amigo?</h1>
                <p className="onboarding-page__subtitle">
                  Adicione os detalhes do seu pet para começarmos a acompanhar a saúde dele de perto.
                </p>
              </div>
            </main>

            <div className="onboarding-page__action-area">
              <div className="onboarding-page__btn-wrapper">
                <Button onClick={handleNext}>
                  Começar
                  <span className="material-symbols-outlined">arrow_forward</span>
                </Button>
              </div>
            </div>
          </div>
        );
      case 2:
        return (
          <div className="onboarding-page__step animate-fade-in">
            <header className="onboarding-page__header">
              <div className="onboarding-page__header-container">
                <button aria-label="Voltar" className="onboarding-page__back-btn" onClick={handleBack}>
                  <span className="material-symbols-outlined">arrow_back</span>
                </button>
                <div className="onboarding-page__header-title">Dados Básicos</div>
              </div>
            </header>
            
            <main className="onboarding-page__form-container">
              <h2 className="onboarding-page__form-title">Qual é o nome do seu pet?</h2>
              
              <FormField 
                label="Nome *" 
                id="pet-name"
                name="name" 
                value={formData.name} 
                onChange={handleChange} 
                placeholder="Ex: Rex"
                required 
              />

              <div className="molecule-form-field">
                <label className="atom-label" htmlFor="pet-species">Espécie *</label>
                <div className="atom-input-wrapper">
                  <select id="pet-species" name="species" value={formData.species} onChange={handleChange} className="atom-input">
                    <option value="DOG">Cachorro</option>
                    <option value="CAT">Gato</option>
                    <option value="BIRD">Pássaro</option>
                    <option value="FISH">Peixe</option>
                    <option value="RODENT">Roedor</option>
                    <option value="REPTILE">Réptil</option>
                    <option value="OTHER">Outro</option>
                  </select>
                </div>
              </div>

              <FormField 
                label="Raça" 
                id="pet-breed"
                name="breed" 
                value={formData.breed} 
                onChange={handleChange} 
                placeholder="Ex: Poodle (Opcional)" 
              />
            </main>

            <div className="onboarding-page__action-area">
              <div className="onboarding-page__btn-wrapper">
                <Button onClick={handleNext} disabled={!formData.name}>
                  Avançar
                  <span className="material-symbols-outlined">arrow_forward</span>
                </Button>
              </div>
            </div>
          </div>
        );
      case 3:
        return (
          <div className="onboarding-page__step animate-fade-in">
            <header className="onboarding-page__header">
              <div className="onboarding-page__header-container">
                <button aria-label="Voltar" className="onboarding-page__back-btn" onClick={handleBack}>
                  <span className="material-symbols-outlined">arrow_back</span>
                </button>
                <div className="onboarding-page__header-title">Saúde e Perfil</div>
              </div>
            </header>
            
            <main className="onboarding-page__form-container">
              <h2 className="onboarding-page__form-title">Detalhes de saúde</h2>

              <FormField 
                label="Peso (kg)" 
                id="pet-weight"
                type="number" 
                name="weightKg" 
                value={formData.weightKg} 
                onChange={handleChange} 
                placeholder="Ex: 5.5" 
                step="0.1"
                min="0"
              />

              <FormField 
                label="Data de Nascimento" 
                id="pet-birth"
                type="date" 
                name="birthDate" 
                value={formData.birthDate} 
                onChange={handleChange} 
              />

              <div className="molecule-form-field">
                <label className="atom-label" htmlFor="pet-sex">Gênero</label>
                <div className="atom-input-wrapper">
                  <select id="pet-sex" name="sex" value={formData.sex} onChange={handleChange} className="atom-input">
                    <option value="UNKNOWN">Não especificado</option>
                    <option value="MALE">Macho</option>
                    <option value="FEMALE">Fêmea</option>
                  </select>
                </div>
              </div>
            </main>

            <div className="onboarding-page__action-area">
              <div className="onboarding-page__btn-wrapper">
                <Button onClick={handleNext}>
                  Avançar
                  <span className="material-symbols-outlined">arrow_forward</span>
                </Button>
              </div>
            </div>
          </div>
        );
      case 4:
        return (
          <div className="onboarding-page__step animate-fade-in">
            <header className="onboarding-page__header">
              <div className="onboarding-page__header-container">
                <button aria-label="Voltar" className="onboarding-page__back-btn" onClick={handleBack} disabled={loading}>
                  <span className="material-symbols-outlined">arrow_back</span>
                </button>
                <div className="onboarding-page__header-title">Foto do Pet</div>
              </div>
            </header>
            
            <main className="onboarding-page__form-container">
              <h2 className="onboarding-page__form-title">Adicione uma foto</h2>
              <p className="onboarding-page__form-subtitle">Deixe o perfil do seu pet com a cara dele!</p>
              
              <div className="onboarding-page__photo-upload">
                <input type="file" id="pet-photo" accept="image/*" onChange={handleFileChange} hidden />
                <label htmlFor="pet-photo" className="onboarding-page__photo-label">
                  {photo ? (
                    <img src={URL.createObjectURL(photo)} alt="Preview" className="onboarding-page__photo-preview" />
                  ) : (
                    <div className="onboarding-page__photo-placeholder">
                      <span className="material-symbols-outlined">add_a_photo</span>
                      <span>Escolher foto</span>
                    </div>
                  )}
                </label>
              </div>

              {error && <div className="onboarding-page__error-msg">{error}</div>}
            </main>

            <div className="onboarding-page__action-area">
              <div className="onboarding-page__btn-wrapper">
                <Button onClick={handleSubmit} isLoading={loading}>
                  Finalizar Cadastro
                  <span className="material-symbols-outlined">check</span>
                </Button>
              </div>
            </div>
          </div>
        );
      case 5:
        return (
          <div className="onboarding-page__step onboarding-page__step--success animate-fade-in">
            <main className="onboarding-page__main">
              <div className="onboarding-page__success-icon">
                <span className="material-symbols-outlined">task_alt</span>
              </div>
              <h1 className="onboarding-page__success-title">Pet adicionado com sucesso!</h1>
              <p className="onboarding-page__success-subtitle">
                Tudo pronto para cuidar do {formData.name || 'seu pet'}.
              </p>
            </main>
            <div className="onboarding-page__action-area">
              <div className="onboarding-page__btn-wrapper">
                <Button onClick={() => navigate('/')}>
                  Ir para o Início
                  <span className="material-symbols-outlined">home</span>
                </Button>
              </div>
            </div>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="onboarding-page">
      {renderStep()}
    </div>
  );
};
export { OnboardingPageContent as OnboardingPage };
