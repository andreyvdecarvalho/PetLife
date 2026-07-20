import React, { useState, useEffect, useRef } from 'react';
import { useCreatePet } from '../../../application/pet/useCreatePet';
import { useUpdatePet } from '../../../application/pet/useUpdatePet';
import { useDeletePet } from '../../../application/pet/useDeletePet';
import type { Pet, PetSex, PetSize, PetSpecies } from '../../../domain/pet/Pet';
import { compressImage } from '../../../utils/imageCompressor';
import { FormField } from '../../molecules/FormField';
import { UploadButton } from '../../molecules/UploadButton';
import { Button } from '../../atoms/Button';
import { usePetWeightHistory } from '../../../application/pet/usePetWeightHistory';
import './styles.css';

interface PetFormProps {
  pet?: Pet;
  onSuccess: () => void;
  onCancel: () => void;
}

const BREED_SUGGESTIONS: Record<string, string[]> = {
  DOG: [
    'Sem raça definida (SRD)',
    'Golden Retriever',
    'Poodle',
    'Buldogue Francês',
    'Pastor Alemão',
    'Yorkshire Terrier',
    'Labrador Retriever',
    'Beagle',
    'Pinscher',
    'Shih Tzu',
    'Rottweiler',
    'Pug',
  ],
  CAT: [
    'Sem raça definida (SRD)',
    'Persa',
    'Siamês',
    'Maine Coon',
    'Angorá',
    'Ragdoll',
    'Sphynx',
    'Bengal',
  ],
  BIRD: ['Calopsita', 'Canário', 'Papagaio', 'Periquito'],
  FISH: ['Beta', 'Peixinho Dourado', 'Guppy', 'Neon'],
};

export const PetForm: React.FC<PetFormProps> = ({ pet, onSuccess, onCancel }) => {
  const { createPet, loading: createLoading, error: createError } = useCreatePet();
  const { updatePet, loading: updateLoading, error: updateError } = useUpdatePet();
  const { deletePet, loading: deleteLoading, error: deleteError } = useDeletePet();

  const loading = createLoading || updateLoading || deleteLoading;
  const apiError = createError || updateError || deleteError;

  const isSubmitting = useRef(false);

  // Campos do formulário
  const [name, setName] = useState('');
  const [species, setSpecies] = useState<PetSpecies>('DOG');
  const [breed, setBreed] = useState('');
  const [sex, setSex] = useState<PetSex>('UNKNOWN');
  const [birthDate, setBirthDate] = useState('');
  const [weightKg, setWeightKg] = useState('');
  const [size, setSize] = useState<PetSize>('MEDIUM');
  const [neutered, setNeutered] = useState(false);
  const [microchipId, setMicrochipId] = useState('');
  const [allergies, setAllergies] = useState('');
  const [notes, setNotes] = useState('');

  // Foto e Compressão
  const [photoFile, setPhotoFile] = useState<File | null>(null);
  const [photoPreview, setPhotoPreview] = useState<string | null>(null);
  const [compressing, setCompressing] = useState(false);
  
  // Histórico de peso
  const { data: weightHistory, loading: weightLoading, deleteWeight, updateWeight } = usePetWeightHistory(pet?.id || '');
  const [editingWeightId, setEditingWeightId] = useState<string | null>(null);
  const [editWeightValue, setEditWeightValue] = useState<string>('');
  const [editWeightDate, setEditWeightDate] = useState<string>('');

  // Validações
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (pet) {
      setName(pet.name);
      setSpecies(pet.species);
      setBreed(pet.breed || '');
      setSex(pet.sex);
      setBirthDate(pet.birthDate || '');
      setWeightKg(pet.weightKg ? String(pet.weightKg) : '');
      setSize(pet.size || 'MEDIUM');
      setNeutered(pet.neutered || false);
      setMicrochipId(pet.microchipId || '');
      setAllergies(pet.allergies || '');
      setNotes(pet.notes || '');
      if (pet.photoUrl) {
        setPhotoPreview(pet.photoUrl);
      }
    }
  }, [pet]);

  // Carrega raças sugeridas dinamicamente
  const breedSuggestions = BREED_SUGGESTIONS[species] || [];

  const handlePhotoChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setCompressing(true);
    try {
      // Comprime localmente antes de salvar no estado
      const compressed = await compressImage(file, 500); // máx 500KB
      setPhotoFile(compressed);
      
      const reader = new FileReader();
      reader.onloadend = () => {
        setPhotoPreview(reader.result as string);
      };
      reader.readAsDataURL(compressed);
    } catch (err) {
      console.error('Erro na compressão:', err);
      setErrors(prev => ({ ...prev, photo: 'Falha ao processar imagem.' }));
    } finally {
      setCompressing(false);
    }
  };

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!name.trim()) {
      newErrors.name = 'O nome é obrigatório.';
    } else if (name.trim().length < 2) {
      newErrors.name = 'O nome deve ter no mínimo 2 caracteres.';
    }

    if (!species) {
      newErrors.species = 'A espécie é obrigatória.';
    }

    if (birthDate) {
      const selectedDate = new Date(birthDate);
      const today = new Date();
      if (selectedDate > today) {
        newErrors.birthDate = 'A data de nascimento não pode ser no futuro.';
      }
    }

    if (weightKg && isNaN(Number(weightKg))) {
      newErrors.weightKg = 'O peso deve ser um número válido.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isSubmitting.current) return;
    if (!validate()) return;
    
    isSubmitting.current = true;

    try {
      const data = {
        name,
        species,
        breed: breed.trim() || undefined,
        sex,
        birthDate: birthDate || undefined,
        weightKg: weightKg ? Number(weightKg) : undefined,
        size,
        neutered,
        microchipId: microchipId.trim() || undefined,
        allergies: allergies.trim() || undefined,
        notes: notes.trim() || undefined,
      };

      if (pet) {
        await updatePet(pet.id, data, photoFile || undefined);
      } else {
        await createPet(data, photoFile || undefined);
      }

      onSuccess();
    } catch (err) {
      // Erro tratado pelos hooks e exposto via apiError
    } finally {
      isSubmitting.current = false;
    }
  };

  const handleDelete = async () => {
    if (!pet) return;
    if (!window.confirm('Tem certeza que deseja excluir este pet? Esta ação não pode ser desfeita.')) return;
    
    isSubmitting.current = true;
    try {
      await deletePet(pet.id);
      onSuccess(); // Triggers navigation back and success message
    } catch (err) {
      // Error handled by hook
    } finally {
      isSubmitting.current = false;
    }
  };

  return (
    <form className="organism-pet-form" onSubmit={handleSubmit} data-testid="pet-form">
      <h2 className="organism-pet-form__title">{pet ? 'Editar Pet' : 'Cadastrar Novo Pet'}</h2>
      
      {apiError && (
        <div className="organism-pet-form__alert-error" role="alert">
          {apiError}
        </div>
      )}

      {/* Foto do Pet */}
      <UploadButton
        photoPreview={photoPreview}
        compressing={compressing}
        error={errors.photo}
        onPhotoChange={handlePhotoChange}
      />

      <div className="organism-pet-form__grid">
        {/* Nome */}
        <FormField
          label="Nome do Pet"
          id="pet-name"
          value={name}
          onChange={e => setName(e.target.value)}
          error={errors.name}
          required
          placeholder="Ex: Max, Luna"
          data-testid="input-nome-pet"
        />

        {/* Espécie */}
        <div className="molecule-form-field">
          <label htmlFor="pet-species" className="atom-label atom-label--required">
            Espécie
          </label>
          <select
            id="pet-species"
            value={species}
            onChange={e => {
              setSpecies(e.target.value as PetSpecies);
              setBreed(''); // Reseta a raça ao mudar a espécie
            }}
            className={`atom-input ${errors.species ? 'atom-input--error' : ''}`}
            data-testid="select-especie-pet"
          >
            <option value="DOG">Cachorro</option>
            <option value="CAT">Gato</option>
            <option value="BIRD">Ave</option>
            <option value="FISH">Peixe</option>
            <option value="RODENT">Roedor</option>
            <option value="REPTILE">Réptil</option>
            <option value="OTHER">Outro</option>
          </select>
          {errors.species && <span className="molecule-form-field__error">{errors.species}</span>}
        </div>

        {/* Raça */}
        <div className="molecule-form-field">
          <label htmlFor="pet-breed" className="atom-label">
            Raça
          </label>
          <input
            id="pet-breed"
            type="text"
            list="breeds-list"
            value={breed}
            onChange={e => setBreed(e.target.value)}
            className="atom-input"
            placeholder="Ex: Golden Retriever, Siamês"
            data-testid="input-raca-pet"
          />
          <datalist id="breeds-list">
            {breedSuggestions.map(suggestion => (
              <option key={suggestion} value={suggestion} />
            ))}
          </datalist>
        </div>

        {/* Sexo */}
        <div className="molecule-form-field">
          <label htmlFor="pet-sex" className="atom-label">
            Sexo
          </label>
          <select
            id="pet-sex"
            value={sex}
            onChange={e => setSex(e.target.value as PetSex)}
            className="atom-input"
            data-testid="select-sexo-pet"
          >
            <option value="MALE">Macho</option>
            <option value="FEMALE">Fêmea</option>
            <option value="UNKNOWN">Não Sei</option>
          </select>
        </div>

        {/* Data de Nascimento */}
        <FormField
          label="Data de Nascimento"
          id="pet-birthdate"
          type="date"
          value={birthDate}
          onChange={e => setBirthDate(e.target.value)}
          error={errors.birthDate}
          data-testid="input-nascimento-pet"
        />

        {/* Peso (kg) */}
        <FormField
          label="Peso (kg)"
          id="pet-weight"
          type="text"
          value={weightKg}
          onChange={e => setWeightKg(e.target.value)}
          error={errors.weightKg}
          placeholder="Ex: 12.5"
          data-testid="input-peso-pet"
        />

        {/* Porte */}
        <div className="molecule-form-field">
          <label htmlFor="pet-size" className="atom-label">
            Porte
          </label>
          <select
            id="pet-size"
            value={size}
            onChange={e => setSize(e.target.value as PetSize)}
            className="atom-input"
            data-testid="select-porte-pet"
          >
            <option value="MINI">Mini</option>
            <option value="SMALL">Pequeno</option>
            <option value="MEDIUM">Médio</option>
            <option value="LARGE">Grande</option>
            <option value="GIANT">Gigante</option>
          </select>
        </div>

        {/* ID do Microchip */}
        <FormField
          label="Nº do Microchip"
          id="pet-microchip"
          value={microchipId}
          onChange={e => setMicrochipId(e.target.value)}
          placeholder="Ex: 981020000..."
          data-testid="input-microchip-pet"
        />
      </div>

      {/* Castrado */}
      <div className="organism-pet-form__checkbox-field">
        <label className="organism-pet-form__checkbox-label">
          <input
            type="checkbox"
            checked={neutered}
            onChange={e => setNeutered(e.target.checked)}
            className="organism-pet-form__checkbox"
            data-testid="checkbox-castrado-pet"
          />
          <span>O pet é castrado</span>
        </label>
      </div>

      {/* Alergias */}
      <div className="molecule-form-field">
        <label htmlFor="pet-allergies" className="atom-label">
          Alergias
        </label>
        <textarea
          id="pet-allergies"
          value={allergies}
          onChange={e => setAllergies(e.target.value)}
          className="atom-input atom-input--textarea"
          placeholder="Ex: Alergia a picada de pulga, ração de frango..."
          rows={3}
          data-testid="input-alergias-pet"
        />
      </div>

      {/* Notas / Observações */}
      <div className="molecule-form-field">
        <label htmlFor="pet-notes" className="atom-label">
          Observações / Notas de Cuidados
        </label>
        <textarea
          id="pet-notes"
          value={notes}
          onChange={e => setNotes(e.target.value)}
          className="atom-input atom-input--textarea"
          placeholder="Ex: Medo de fogos, rotina de passeios..."
          rows={3}
          data-testid="input-observacoes-pet"
        />
      </div>

      {/* Histórico de Peso */}
      {pet && (
        <div className="organism-pet-form__weight-history">
          <h3 className="organism-pet-form__subtitle">Histórico de Peso</h3>
          {weightLoading ? (
            <p className="organism-pet-form__weight-loading">Carregando histórico...</p>
          ) : weightHistory.length === 0 ? (
            <p className="organism-pet-form__weight-empty">Nenhum registro de peso.</p>
          ) : (
            <div className="organism-pet-form__weight-list">
              {weightHistory.map(record => (
                <div key={record.id} className="organism-pet-form__weight-item">
                  {editingWeightId === record.id ? (
                    <div className="organism-pet-form__weight-edit-row">
                      <input 
                        type="date" 
                        value={editWeightDate}
                        onChange={e => setEditWeightDate(e.target.value)}
                        className="atom-input"
                      />
                      <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                        <input 
                          type="number" 
                          step="0.01"
                          value={editWeightValue}
                          onChange={e => setEditWeightValue(e.target.value)}
                          className="atom-input"
                          style={{ width: '80px' }}
                        />
                        <span style={{ fontSize: '14px', color: 'var(--color-on-surface-variant)' }}>kg</span>
                      </div>
                      <div className="organism-pet-form__weight-actions">
                        <button 
                          type="button" 
                          onClick={async () => {
                            try {
                              if (!editWeightValue || isNaN(Number(editWeightValue))) {
                                alert('Peso inválido.');
                                return;
                              }
                              if (!editWeightDate) {
                                alert('Data inválida.');
                                return;
                              }
                              await updateWeight(record.id, Number(editWeightValue), new Date(editWeightDate).toISOString());
                              setEditingWeightId(null);
                            } catch (err: any) {
                              alert(err.message);
                            }
                          }}
                          className="organism-pet-form__icon-btn success"
                          title="Salvar"
                        >
                          <span className="material-symbols-outlined">check</span>
                        </button>
                        <button 
                          type="button" 
                          onClick={() => setEditingWeightId(null)}
                          className="organism-pet-form__icon-btn cancel"
                          title="Cancelar"
                        >
                          <span className="material-symbols-outlined">close</span>
                        </button>
                      </div>
                    </div>
                  ) : (
                    <>
                      <div className="organism-pet-form__weight-info">
                        <span className="organism-pet-form__weight-date">
                          {new Date(record.recordedAt).toLocaleDateString('pt-BR')}
                        </span>
                        <span className="organism-pet-form__weight-value">
                          {record.weightKg} kg
                        </span>
                      </div>
                      <div className="organism-pet-form__weight-actions">
                        <button 
                          type="button" 
                          onClick={() => {
                            setEditingWeightId(record.id);
                            setEditWeightValue(String(record.weightKg));
                            setEditWeightDate(record.recordedAt.split('T')[0]);
                          }}
                          className="organism-pet-form__icon-btn edit"
                          title="Editar"
                        >
                          <span className="material-symbols-outlined">edit</span>
                        </button>
                        <button 
                          type="button" 
                          onClick={async () => {
                            if (window.confirm('Excluir este registro de peso?')) {
                              try {
                                await deleteWeight(record.id);
                              } catch (err: any) {
                                alert(err.message);
                              }
                            }
                          }}
                          className="organism-pet-form__icon-btn delete"
                          title="Excluir"
                        >
                          <span className="material-symbols-outlined">delete</span>
                        </button>
                      </div>
                    </>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Ações */}
      <div className="organism-pet-form__actions">
        {pet && (
          <Button 
            type="button" 
            variant="danger" 
            onClick={handleDelete}
            disabled={loading}
          >
            Excluir Pet
          </Button>
        )}
        <Button 
          type="button" 
          variant="secondary" 
          onClick={onCancel}
          disabled={loading}
          data-testid="btn-cancelar-pet"
        >
          Cancelar
        </Button>
        <Button 
          type="submit" 
          disabled={loading || compressing}
          data-testid="btn-salvar-pet"
        >
          {loading ? (pet ? 'Salvando...' : 'Cadastrando...') : (pet ? 'Salvar Alterações' : 'Cadastrar Pet')}
        </Button>
      </div>
    </form>
  );
};
