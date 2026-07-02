import React, { useEffect, useState } from 'react';
import { useVaccinations } from '../../../application/vaccination/useVaccinations';
import { useVaccineSuggestions } from '../../../application/vaccination/useVaccineSuggestions';
import { VaccineList } from '../VaccineList';
import { VaccineForm } from '../VaccineForm';
import type { CreateVaccinationData, Vaccination } from '../../../domain/pet/Vaccination';
import { Modal } from '../../molecules/Modal';
import { Button } from '../../atoms/Button';
import { ProofUploader } from '../../molecules/ProofUploader';
import './styles.css';

interface VaccinationsTabProps {
  petId: string;
  species?: string;
}

export const VaccinationsTab: React.FC<VaccinationsTabProps> = ({ petId, species }) => {
  const { vaccinations, loading, error, fetchVaccinations, addVaccination, updateVaccination, uploadProof } = useVaccinations(petId);
  const { suggestions, fetchSuggestions } = useVaccineSuggestions(species);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingVaccine, setEditingVaccine] = useState<Vaccination | null>(null);

  useEffect(() => {
    if (petId) {
      fetchVaccinations();
    }
  }, [petId, fetchVaccinations]);

  useEffect(() => {
    if (species) {
      fetchSuggestions();
    }
  }, [species, fetchSuggestions]);

  const handleOpenNew = () => {
    setEditingVaccine(null);
    setIsModalOpen(true);
  };

  const handleOpenEdit = (vaccine: Vaccination) => {
    setEditingVaccine(vaccine);
    setIsModalOpen(true);
  };

  const handleSubmit = async (data: CreateVaccinationData) => {
    let success = false;
    if (editingVaccine) {
      success = await updateVaccination(editingVaccine.id, data);
    } else {
      success = await addVaccination(data);
    }
    if (success) {
      setIsModalOpen(false);
    }
  };

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0] && editingVaccine) {
      await uploadProof(editingVaccine.id, e.target.files[0]);
      // The uploadProof will refetch vaccinations, but we need to update the editingVaccine proofUrl to show immediately
      const fakeUrl = URL.createObjectURL(e.target.files[0]);
      setEditingVaccine(prev => prev ? { ...prev, proofUrl: fakeUrl } : null);
    }
  };

  return (
    <div className="organism-vaccinations-tab">
      <div className="organism-vaccinations-tab__header">
        <h3 className="organism-vaccinations-tab__title">Histórico de Vacinas</h3>
        <Button onClick={handleOpenNew} size="small">
          <span className="material-symbols-outlined">add</span>
          Nova Vacina
        </Button>
      </div>
      
      {error && <div className="organism-vaccinations-tab__error">{error}</div>}

      {loading && !vaccinations.length ? (
        <div className="organism-vaccinations-tab__loading">Carregando vacinas...</div>
      ) : (
        <VaccineList 
          vaccinations={vaccinations} 
          onVaccineClick={handleOpenEdit}
        />
      )}

      <Modal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)}
        title={editingVaccine ? 'Detalhes da Vacina' : 'Registrar Nova Vacina'}
      >
        <div className="organism-vaccinations-tab__modal-content">
          <VaccineForm 
            initialData={editingVaccine || undefined}
            suggestions={suggestions}
            onSubmit={handleSubmit}
            onCancel={() => setIsModalOpen(false)}
            loading={loading}
          />

          {editingVaccine && (
            <div className="organism-vaccinations-tab__proof-section">
              <h4>Comprovante de Vacinação</h4>
              <ProofUploader 
                proofPreview={editingVaccine.proofUrl || null}
                loading={loading}
                onFileChange={handleFileChange}
              />
            </div>
          )}
        </div>
      </Modal>
    </div>
  );
};
