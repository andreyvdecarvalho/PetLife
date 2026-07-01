import React from 'react';
import { useNavigate } from 'react-router-dom';
import './styles.css';

export const OnboardingPageContent: React.FC = () => {
  const navigate = useNavigate();

  const handleStart = () => {
    navigate('/register');
  };

  return (
    <div className="onboarding-page animate-fade-in">
      {/* TopAppBar */}
      <header className="onboarding-page__header">
        <div className="onboarding-page__header-container">
          <button 
            aria-label="Voltar" 
            className="onboarding-page__back-btn"
            onClick={() => navigate('/login')}
          >
            <span className="material-symbols-outlined">arrow_back</span>
          </button>
          <div className="onboarding-page__header-logo">
            PetLife
          </div>
        </div>
      </header>

      {/* Main Canvas */}
      <main className="onboarding-page__main">
        {/* Illustration */}
        <div className="onboarding-page__illustration">
          <div className="onboarding-page__gradient-overlay"></div>
          <img 
            alt="A cheerful and warm photo of a happy dog looking up" 
            className="onboarding-page__img" 
            src="https://lh3.googleusercontent.com/aida-public/AB6AXuB5ZoSUBouSGKvObWqbmdYl9jl9EWGYayCnDddGABFlwOeXXZTFPiWE8-hBSnjs-P04txevwdB5y4dGGFA5wEJKc5r_V_mH6RwJrv5cjLYvBo39WMBIJUErJ_PjyEklM5YMFvAp3Wjbg46MW0jGyTpRvsT84lWKACulqXyrDQ70Vk9vQrychqmzzYGEzpsJcb0dt8ah6s_UJJXgS4HraXFRgVEFcdKCrgPVOAPOCusmLrMoDP-8jm18ZBBh6KBBG5QRCjsDgaYckcs" 
          />
          {/* Floating Decorative Elements */}
          <div className="onboarding-page__floating onboarding-page__floating--favorite">
            <span className="material-symbols-outlined text-primary filled">favorite</span>
          </div>
          <div className="onboarding-page__floating onboarding-page__floating--pets">
            <span className="material-symbols-outlined text-on-tertiary-container filled">pets</span>
          </div>
        </div>

        {/* Typography */}
        <div className="onboarding-page__text-container">
          <h1 className="onboarding-page__title">
            Vamos conhecer o seu melhor amigo?
          </h1>
          <p className="onboarding-page__subtitle">
            Adicione os detalhes do seu pet para começarmos a acompanhar a saúde dele de perto.
          </p>
        </div>
      </main>

      {/* Bottom Action Area (Fixed) */}
      <div className="onboarding-page__action-area">
        <button className="onboarding-page__start-btn group" onClick={handleStart}>
          <span>Começar</span>
          <span className="material-symbols-outlined">arrow_forward</span>
        </button>
      </div>
    </div>
  );
};
export { OnboardingPageContent as OnboardingPage };
