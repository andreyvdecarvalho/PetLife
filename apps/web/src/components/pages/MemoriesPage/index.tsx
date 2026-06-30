import React, { useState } from 'react';
import { useToast } from '../../molecules/Toast';
import './styles.css';

interface Memory {
  id: string;
  title: string;
  category: 'celebration' | 'adventure' | 'adoption';
  categoryLabel: string;
  categoryIcon: string;
  date: string;
  description: string;
  imageUrl: string;
}

export const MemoriesPageContent: React.FC = () => {
  const { showToast } = useToast();

  const [memories, setMemories] = useState<Memory[]>([
    {
      id: 'mem-1',
      title: 'Aniversário de 1 ano',
      category: 'celebration',
      categoryLabel: 'Celebração',
      categoryIcon: 'cake',
      date: '15 de Abril, 2022',
      description: 'Uma festa inesquecível no parque com direito a bolo especial para cães e muitos amigos de quatro patas!',
      imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDUZO3bE7L7aqKa-6cl_SeteIWlbW-O2C3RDo1i1CeNxWRmlovuC_lknOxY_leSrFkR6xuieLQ4rWMwGE3etaKZRviBDfQ_-VHhmGX8KrMbJ3bNCNzEXeMgijtgijS6SdmdTyBmY_bI5hPVcRr-Hl7kdpF4YGKu9zcJ9mgXnZtRxeKJIDcXqLHAafKk0MQSoC8LoSs5sqNjwTaI_KScR24vdwENm1SU-cfSXbrQ18g9PGjKJbKmCguXUh_M-Zc2f0ov_C-1Z3d25rk'
    },
    {
      id: 'mem-2',
      title: 'Primeira ida à praia',
      category: 'adventure',
      categoryLabel: 'Aventura',
      categoryIcon: 'waves',
      date: '10 de Janeiro, 2022',
      description: 'Bella descobriu a areia e o mar! No começo ficou com medo das ondas, mas logo estava correndo solta.',
      imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCoGem71uS-xEgw5quv8zXbwGJD6RMdQpcG1ZNphhizVZIq-HW7_dW5Ukm6XhUDgsoPXykKdeOxOCzoPHtquuSGDGWnY2o6XnF2TaEZ99VwZwCqLvMll8P3sscvmkEjGCzemMOLvFarRQdjFTlGoTn-G-ZAALbpmHejvvhw3pAqQVcLhI1kB_8fmNNgJtNvffQPOcpWgE7ksQnhgnzYDq0R9r4MZPy5DIhf3a8YGNl_cGbevdpwSHKxi7h_EQSIC3-5MvzAXwODXWY'
    },
    {
      id: 'mem-3',
      title: 'Primeiro dia em casa',
      category: 'adoption',
      categoryLabel: 'Adoção',
      categoryIcon: 'home',
      date: '20 de Abril, 2021',
      description: 'O dia em que nossa família ficou completa. Ela era tão pequena e já dominou o sofá inteiro.',
      imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBqRniJBe7iJxS9ozB2uUL-WgBpnVcJ1_LzKNnQ8x10JqMB7I2g7Nax6Zze4Q5CVKHGnEmkjLBuXsB6gbyVPl4fR2KRkv24bsbziMom1he2TB7lZEyTS1gb74BGDz8UISsCxxBPmVuQsuOlUSK-61zN528f1DAvkY52Jj-LCyUCkpJGJ653phm9AL_Rg_Ph1DwKXGtByjsEewXVM-OjlqHuNQpUVAf3FwiaaQmKknFWUwBQ9rbXPrMMQew_UfKdP969jnWDn5iblKI'
    }
  ]);

  const handleAddPhoto = () => {
    showToast('Funcionalidade de upload de fotos em breve! 📸', 'info');
  };

  const handleAddMemory = () => {
    showToast('Diário de Memórias: Nova memória adicionada! ✨', 'success');
    
    const newMem: Memory = {
      id: `mem-${Date.now()}`,
      title: 'Novo Momento Feliz',
      category: 'adventure',
      categoryLabel: 'Aventura',
      categoryIcon: 'pets',
      date: 'Hoje',
      description: 'Acabamos de registrar mais um momento incrível juntos!',
      imageUrl: 'https://lh3.googleusercontent.com/aida-public/AB6AXuD3egj02EFcm7YPJdS_rDDQLiVBlZ6emdq9GTjgfsnlQxxqzZq8eA4-Jav89DLn4DImsELsi1PLmB0dHl3IK6WE_ZZ8jhcr5MlOPKKoDoZxWw2uHm3goIzXrozsMf5QVSjk4m7_BIzqBq1MFrnGFYgre2PAFyObzjLoAXPCLsLZtiI6_17SYARlSIW8qlTUw-kLXpyHFdw9oHyO1W1cInjHXlLkri5UIlLR6y8lgjlWDxH-7eo0EWxWIybLQXLebmCP0mBvvA1IIEc'
    };

    setMemories(prev => [newMem, ...prev]);
  };

  return (
    <div className="memories-page animate-fade-in">
      {/* Header Section */}
      <section className="memories-page__header">
        <h2 className="memories-page__title">Diary</h2>
        <p className="memories-page__subtitle">Capture os momentos inesquecíveis da vida do seu pet.</p>
      </section>

      {/* Bento Grid: Growth Comparison (Hero Feature) */}
      <section className="memories-page__hero">
        <div className="memories-page__hero-card glass-card">
          <div className="memories-page__hero-header">
            <div className="memories-page__hero-title-group">
              <h3 className="memories-page__hero-title">
                <span className="material-symbols-outlined filled">auto_awesome</span>
                Veja o quanto Bella cresceu!
              </h3>
              <p className="memories-page__hero-subtitle">2 meses vs 2 anos</p>
            </div>
            <button className="memories-page__add-photo-btn" onClick={handleAddPhoto}>
              Adicionar Foto
            </button>
          </div>

          <div className="memories-page__hero-grid">
            {/* Puppy Image */}
            <div className="memories-page__hero-img-wrapper img-hover-zoom">
              <img 
                alt="Puppy Bella" 
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuD3egj02EFcm7YPJdS_rDDQLiVBlZ6emdq9GTjgfsnlQxxqzZq8eA4-Jav89DLn4DImsELsi1PLmB0dHl3IK6WE_ZZ8jhcr5MlOPKKoDoZxWw2uHm3goIzXrozsMf5QVSjk4m7_BIzqBq1MFrnGFYgre2PAFyObzjLoAXPCLsLZtiI6_17SYARlSIW8qlTUw-kLXpyHFdw9oHyO1W1cInjHXlLkri5UIlLR6y8lgjlWDxH-7eo0EWxWIybLQXLebmCP0mBvvA1IIEc" 
              />
              <div className="memories-page__hero-label">
                <span className="material-symbols-outlined">calendar_month</span>
                Abril, 2021
              </div>
            </div>
            {/* Adult Image */}
            <div className="memories-page__hero-img-wrapper img-hover-zoom">
              <img 
                alt="Adult Bella" 
                src="https://lh3.googleusercontent.com/aida-public/AB6AXuAzDGBdbYTcHwg2JpX0VUxXcONOgtC7jTiDs549G6HnMUeixcVdRI03sTWIiLGCQF9zvAtLl5PKVAUVbpuv0OojJc-6MPpSIOfPNkD48qFPWPLTOHXEfZLIUBwLFdIoggxkq2XwrXtdvp_x0wAHQpYON5GXRtiqvpyH2NYLyJCb9vV8T-Ob-he8-r3QQnOgKMoM9EsJhnV-5scA8LisPixf2mOFyyndYSbThJqE3PZzeLaku3_ehCyu5xuFUQb_ypaDt9_ANnmCiwE" 
              />
              <div className="memories-page__hero-label memories-page__hero-label--right">
                <span className="material-symbols-outlined">calendar_month</span>
                Hoje
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Vertical Timeline Section */}
      <section className="memories-page__timeline-section">
        {/* Central Line (Desktop) */}
        <div className="memories-page__timeline-line"></div>
        {/* Mobile Line */}
        <div className="memories-page__timeline-line-mobile"></div>

        <div className="memories-page__timeline-entries">
          {memories.map((mem, index) => {
            const isOdd = index % 2 === 0; // Alternates layout side: 0, 2 are odd (right side card, text right aligned), 1, 3 are even
            
            return (
              <article 
                key={mem.id} 
                className={`memories-page__timeline-entry ${isOdd ? 'odd' : 'even'}`}
              >
                {/* Dot */}
                <div className={`memories-page__timeline-dot bg-${mem.category}`}></div>

                {/* Content Card */}
                <div className="memories-page__timeline-container">
                  <div className="memories-page__timeline-content glass-card">
                    <div className="memories-page__card-meta">
                      <span className={`memories-page__category-chip medications-page__category-chip--${mem.category}`}>
                        <span className="material-symbols-outlined">{mem.categoryIcon}</span>
                        {mem.categoryLabel}
                      </span>
                      <time className="memories-page__card-time">{mem.date}</time>
                    </div>
                    <h4 className="memories-page__card-title">{mem.title}</h4>
                    <p className="memories-page__card-description">{mem.description}</p>
                    <div className="memories-page__card-img-wrapper img-hover-zoom">
                      <img alt={mem.title} src={mem.imageUrl} />
                    </div>
                  </div>
                </div>
              </article>
            );
          })}
        </div>

        {/* Timeline End Indicator */}
        <div className="memories-page__timeline-end">
          <div className="memories-page__end-badge">
            <span className="material-symbols-outlined">pets</span>
            Início da Jornada
          </div>
        </div>
      </section>

      {/* Floating Action Button (FAB) */}
      <button 
        className="memories-page__fab" 
        onClick={handleAddMemory}
        aria-label="Adicionar nova memória"
      >
        <span className="material-symbols-outlined">add</span>
      </button>
    </div>
  );
};
export { MemoriesPageContent as MemoriesPage };
