import React, { useEffect, useState } from 'react';
import { useSearchVeterinarians } from '../../../application/veterinarian/useSearchVeterinarians';
import { VetCard } from '../../molecules/VetCard';
import './styles.css';

export function VetSearchPage() {
  const { search, data, loading, error } = useSearchVeterinarians();
  const [modality, setModality] = useState<string>('');
  const [emergency, setEmergency] = useState(false);
  const [specialty, setSpecialty] = useState<string>('');
  const [searchTerm, setSearchTerm] = useState('');
  
  const [radiusKm, setRadiusKm] = useState(10);
  const [lat, setLat] = useState<number>();
  const [lng, setLng] = useState<number>();

  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((pos) => {
        setLat(pos.coords.latitude);
        setLng(pos.coords.longitude);
        search({ lat: pos.coords.latitude, lng: pos.coords.longitude, radiusKm, modality: modality || undefined, emergency: emergency ? true : undefined, specialty: specialty || undefined });
      }, () => {
        search({ radiusKm, modality: modality || undefined, emergency: emergency ? true : undefined, specialty: specialty || undefined });
      });
    } else {
      search({ radiusKm, modality: modality || undefined, emergency: emergency ? true : undefined, specialty: specialty || undefined });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Update on filter changes
  useEffect(() => {
    search({ lat, lng, radiusKm, modality: modality || undefined, emergency: emergency ? true : undefined, specialty: specialty || undefined });
  }, [modality, emergency, specialty, search, lat, lng, radiusKm]);

  // Local filtering for searchTerm if API doesn't support it
  const filteredData = data?.content?.filter(vet => 
    !searchTerm || vet.fullName.toLowerCase().includes(searchTerm.toLowerCase())
  ) || [];

  return (
    <div className="vet-search-page">
      <header className="vet-search-page__header">
        <h2>Encontrar Veterinário</h2>
        <p>Busque profissionais qualificados próximos a você</p>
      </header>

      <main className="vet-search-page__main">
      <div className="vet-search-page__search-bar">
        <span className="material-symbols-outlined vet-search-page__search-icon">search</span>
        <input 
          type="text" 
          className="vet-search-page__search-input"
          placeholder="Buscar por nome ou clínica"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      <section className="vet-search-page__filters">
        <button 
          className={`vet-search-page__filter-chip vet-search-page__filter-chip--emergency ${emergency ? 'active' : ''}`}
          onClick={() => setEmergency(!emergency)}
        >
          <span className="material-symbols-outlined" style={{ fontSize: '18px' }}>emergency</span>
          Emergência 24h
        </button>

        <div style={{ width: '1px', height: '24px', backgroundColor: 'var(--color-outline-variant)', margin: '0 4px' }} />

        <button 
          className={`vet-search-page__filter-chip ${specialty === 'Cães' ? 'active' : ''}`}
          onClick={() => setSpecialty(specialty === 'Cães' ? '' : 'Cães')}
        >
          Cães
        </button>
        <button 
          className={`vet-search-page__filter-chip ${specialty === 'Gatos' ? 'active' : ''}`}
          onClick={() => setSpecialty(specialty === 'Gatos' ? '' : 'Gatos')}
        >
          Gatos
        </button>
        <button 
          className={`vet-search-page__filter-chip ${specialty === 'Exóticos' ? 'active' : ''}`}
          onClick={() => setSpecialty(specialty === 'Exóticos' ? '' : 'Exóticos')}
        >
          Exóticos
        </button>

        <div style={{ width: '1px', height: '24px', backgroundColor: 'var(--color-outline-variant)', margin: '0 4px' }} />

        <button 
          className={`vet-search-page__filter-chip ${modality === 'CLINIC' ? 'active' : ''}`}
          onClick={() => setModality(modality === 'CLINIC' ? '' : 'CLINIC')}
        >
          Clínica
        </button>
        <button 
          className={`vet-search-page__filter-chip ${modality === 'HOME' ? 'active' : ''}`}
          onClick={() => setModality(modality === 'HOME' ? '' : 'HOME')}
        >
          Domicílio
        </button>
      </section>

      {error && <p className="vet-search-page__error">{error}</p>}
      {loading && <p className="vet-search-page__loading">Buscando veterinários...</p>}

      <section className="vet-search-page__results">
        {!loading && !error && filteredData.map(vet => (
          <VetCard key={vet.id} veterinarian={vet} />
        ))}
        {!loading && !error && filteredData.length === 0 && (
          <p className="vet-search-page__empty">Nenhum veterinário encontrado com estes filtros.</p>
        )}
      </section>
      </main>
    </div>
  );
}
