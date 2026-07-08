import React, { useState } from 'react';
import { useGroomingForm } from '../../../application/grooming/useGroomingForm';
import type { Grooming, GroomingType } from '../../../domain/pet/Grooming';
import type { CreateGroomingData } from '../../../infrastructure/http/grooming.api';
import './styles.css';

interface GroomingFormProps {
  grooming?: Grooming;
  onSuccess: (data: CreateGroomingData, beforeFile: File | null, afterFile: File | null) => Promise<void>;
  onCancel: () => void;
}

export const GroomingForm: React.FC<GroomingFormProps> = ({ grooming, onSuccess, onCancel }) => {
  const [beforeFile, setBeforeFile] = useState<File | null>(null);
  const [beforePreview, setBeforePreview] = useState<string | null>(
    grooming?.photos && grooming.photos[0] ? grooming.photos[0] : null
  );

  const [afterFile, setAfterFile] = useState<File | null>(null);
  const [afterPreview, setAfterPreview] = useState<string | null>(
    grooming?.photos && grooming.photos[1] ? grooming.photos[1] : null
  );

  const [beforeError, setBeforeError] = useState<string | null>(null);
  const [afterError, setAfterError] = useState<string | null>(null);

  const handleBeforeFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        setBeforeError('A imagem não deve exceder 5MB.');
        return;
      }
      setBeforeError(null);
      setBeforeFile(file);
      setBeforePreview(URL.createObjectURL(file));
    }
  };

  const handleAfterFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        setAfterError('A imagem não deve exceder 5MB.');
        return;
      }
      setAfterError(null);
      setAfterFile(file);
      setAfterPreview(URL.createObjectURL(file));
    }
  };

  const handleFormSubmit = async (values: CreateGroomingData) => {
    await onSuccess(values, beforeFile, afterFile);
  };

  const {
    type,
    setType,
    date,
    setDate,
    provider,
    setProvider,
    cost,
    setCost,
    frequencyDays,
    setFrequencyDays,
    notes,
    setNotes,
    errors,
    isSubmitting,
    handleSubmit,
  } = useGroomingForm({
    initialValues: grooming ? {
      type: grooming.type,
      date: grooming.date,
      provider: grooming.provider,
      cost: grooming.cost,
      frequencyDays: grooming.frequencyDays,
      notes: grooming.notes,
    } : undefined,
    onSubmit: handleFormSubmit,
  });

  return (
    <form className="organism-grooming-form" onSubmit={handleSubmit} noValidate>
      <h3 className="organism-grooming-form__title">
        {grooming ? 'Editar Banho e Tosa' : 'Registrar Banho e Tosa'}
      </h3>

      {/* Tipo de Serviço */}
      <div className="organism-grooming-form__field">
        <label className="organism-grooming-form__label">Tipo de Serviço *</label>
        <div className="organism-grooming-form__radio-group">
          {(['BATH', 'GROOMING', 'BATH_AND_GROOMING'] as GroomingType[]).map((t) => (
            <label 
              key={t} 
              className={`organism-grooming-form__radio-label ${type === t ? 'active' : ''}`}
            >
              <input
                type="radio"
                name="groomingType"
                value={t}
                checked={type === t}
                onChange={() => setType(t)}
                className="organism-grooming-form__radio-input"
                data-testid={`radio-type-${t.toLowerCase()}`}
              />
              <span>
                {t === 'BATH' ? 'Banho' : t === 'GROOMING' ? 'Tosa' : 'Banho & Tosa'}
              </span>
            </label>
          ))}
        </div>
        {errors.type && (
          <span className="organism-grooming-form__error" role="alert">
            {errors.type}
          </span>
        )}
      </div>

      {/* Data */}
      <div className="organism-grooming-form__field">
        <label htmlFor="grooming-date" className="organism-grooming-form__label">Data *</label>
        <input
          id="grooming-date"
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          className="organism-grooming-form__input"
          aria-invalid={!!errors.date}
          aria-describedby={errors.date ? 'grooming-date-error' : undefined}
          data-testid="input-date"
        />
        {errors.date && (
          <span id="grooming-date-error" className="organism-grooming-form__error" role="alert">
            {errors.date}
          </span>
        )}
      </div>

      {/* Pet Shop */}
      <div className="organism-grooming-form__field">
        <label htmlFor="grooming-provider" className="organism-grooming-form__label">Pet Shop / Prestador</label>
        <input
          id="grooming-provider"
          type="text"
          value={provider}
          onChange={(e) => setProvider(e.target.value)}
          placeholder="Ex: Pet Shop Vida Animal"
          className="organism-grooming-form__input"
          data-testid="input-provider"
        />
      </div>

      {/* Custo */}
      <div className="organism-grooming-form__field">
        <label htmlFor="grooming-cost" className="organism-grooming-form__label">Custo (R$)</label>
        <input
          id="grooming-cost"
          type="number"
          step="0.01"
          value={cost}
          onChange={(e) => setCost(e.target.value)}
          placeholder="Ex: 85.00"
          className="organism-grooming-form__input"
          aria-invalid={!!errors.cost}
          aria-describedby={errors.cost ? 'grooming-cost-error' : undefined}
          data-testid="input-cost"
        />
        {errors.cost && (
          <span id="grooming-cost-error" className="organism-grooming-form__error" role="alert">
            {errors.cost}
          </span>
        )}
      </div>

      {/* Periodicidade */}
      <div className="organism-grooming-form__field">
        <label htmlFor="grooming-frequency" className="organism-grooming-form__label">Refazer a cada (dias)</label>
        <input
          id="grooming-frequency"
          type="number"
          value={frequencyDays}
          onChange={(e) => setFrequencyDays(e.target.value)}
          placeholder="Ex: 15"
          className="organism-grooming-form__input"
          aria-invalid={!!errors.frequencyDays}
          aria-describedby={errors.frequencyDays ? 'grooming-frequency-error' : undefined}
          data-testid="input-frequency"
        />
        {errors.frequencyDays && (
          <span id="grooming-frequency-error" className="organism-grooming-form__error" role="alert">
            {errors.frequencyDays}
          </span>
        )}
      </div>

      {/* Notas */}
      <div className="organism-grooming-form__field">
        <label htmlFor="grooming-notes" className="organism-grooming-form__label">Observações / Notas</label>
        <textarea
          id="grooming-notes"
          value={notes}
          onChange={(e) => setNotes(e.target.value)}
          placeholder="Ex: Utilizar shampoo hipoalergênico."
          className="organism-grooming-form__textarea"
          data-testid="textarea-notes"
        />
      </div>

      {/* Uploads de Fotos (Antes e Depois) */}
      <div className="organism-grooming-form__photos-section">
        <h4 className="organism-grooming-form__subtitle">Fotos de Acompanhamento</h4>
        <div className="organism-grooming-form__uploads-grid">
          {/* Foto de Antes */}
          <div className="organism-grooming-form__upload-box">
            <span className="organism-grooming-form__upload-title">Antes</span>
            <label className="organism-grooming-form__upload-area" data-testid="upload-area-before">
              {beforePreview ? (
                <img src={beforePreview} alt="Preview Antes" className="organism-grooming-form__preview-img" />
              ) : (
                <div className="organism-grooming-form__upload-placeholder">
                  <span className="material-symbols-outlined">add_a_photo</span>
                  <span>Enviar Foto</span>
                </div>
              )}
              <input
                type="file"
                accept="image/*"
                onChange={handleBeforeFileChange}
                className="organism-grooming-form__file-input"
                data-testid="file-before"
              />
            </label>
            {beforeError && <span className="organism-grooming-form__error">{beforeError}</span>}
          </div>

          {/* Foto de Depois */}
          <div className="organism-grooming-form__upload-box">
            <span className="organism-grooming-form__upload-title">Depois</span>
            <label className="organism-grooming-form__upload-area" data-testid="upload-area-after">
              {afterPreview ? (
                <img src={afterPreview} alt="Preview Depois" className="organism-grooming-form__preview-img" />
              ) : (
                <div className="organism-grooming-form__upload-placeholder">
                  <span className="material-symbols-outlined">add_a_photo</span>
                  <span>Enviar Foto</span>
                </div>
              )}
              <input
                type="file"
                accept="image/*"
                onChange={handleAfterFileChange}
                className="organism-grooming-form__file-input"
                data-testid="file-after"
              />
            </label>
            {afterError && <span className="organism-grooming-form__error">{afterError}</span>}
          </div>
        </div>
      </div>

      {/* Botões */}
      <div className="organism-grooming-form__actions">
        <button
          type="button"
          onClick={onCancel}
          className="organism-grooming-form__btn organism-grooming-form__btn--secondary"
          disabled={isSubmitting}
          data-testid="btn-cancel"
        >
          Cancelar
        </button>
        <button
          type="submit"
          className="organism-grooming-form__btn organism-grooming-form__btn--primary"
          disabled={isSubmitting}
          data-testid="btn-submit"
        >
          {isSubmitting ? 'Salvando...' : grooming ? 'Salvar Alterações' : 'Registrar Serviço'}
        </button>
      </div>
    </form>
  );
};
