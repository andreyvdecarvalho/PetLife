import React, { useEffect, useState } from 'react';
import { useSearchVeterinarians } from '../../../application/veterinarian/useSearchVeterinarians';
import { VetCard } from '../../molecules/VetCard';
import { Input } from '../../atoms/Input';
import { Button } from '../../atoms/Button';
import './styles.css';

export function VetSearchPage() {
  const { search, data, loading, error } = useSearchVeterinarians();
  const [modality, setModality] = useState<string>('');
  const [emergency, setEmergency] = useState(false);
  const [radiusKm, setRadiusKm] = useState(10);
  const [lat, setLat] = useState<number>();
  const [lng, setLng] = useState<number>();

  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((pos) => {
        setLat(pos.coords.latitude);
        setLng(pos.coords.longitude);
        search({ lat: pos.coords.latitude, lng: pos.coords.longitude, radiusKm, modality: modality || undefined, emergency: emergency ? true : undefined });
      }, () => {
        search({ radiusKm, modality: modality || undefined, emergency: emergency ? true : undefined });
      });
    } else {
      search({ radiusKm, modality: modality || undefined, emergency: emergency ? true : undefined });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleFilter = () => {
    search({ lat, lng, radiusKm, modality: modality || undefined, emergency: emergency ? true : undefined });
  };

  return (
    <div className="vet-search-page">
      <header className="vet-search-page__header">
        <h2>Busca de Veterinários</h2>
        <p>Encontre o melhor profissional próximo a você</p>
      </header>

      <section className="vet-search-page__filters">
        <Input 
          type="number" 
          label="Raio (km)" 
          value={radiusKm} 
          onChange={(e) => setRadiusKm(Number(e.target.value))} 
        />
        <div className="atom-input-wrapper">
          <label className="atom-input-label">Modalidade</label>
          <select 
            className="vet-search-page__select atom-input" 
            value={modality} 
            onChange={(e) => setModality(e.target.value)}
          >
            <option value="">Todas</option>
            <option value="CLINIC">Clínica</option>
            <option value="HOME">Domicílio</option>
            <option value="ONLINE">Online</option>
          </select>
        </div>
        <label className="vet-search-page__checkbox-label">
          <input 
            type="checkbox" 
            checked={emergency} 
            onChange={(e) => setEmergency(e.target.checked)} 
          />
          Plantão Emergência
        </label>
        <Button onClick={handleFilter}>Filtrar</Button>
      </section>

      {error && <p className="vet-search-page__error">{error}</p>}
      {loading && <p className="vet-search-page__loading">Buscando veterinários...</p>}

      <section className="vet-search-page__results">
        {data?.content?.map(vet => (
          <VetCard key={vet.id} veterinarian={vet} />
        ))}
        {!loading && data?.content?.length === 0 && (
          <p className="vet-search-page__empty">Nenhum veterinário encontrado com estes filtros.</p>
        )}
      </section>
    </div>
  );
}
