import React, { useState, useEffect } from 'react';
import type { CreateVaccinationData } from '../../../domain/pet/Vaccination';
import { Input } from '../../atoms/Input';
import { Button } from '../../atoms/Button';
import './styles.css';

interface VaccineFormProps {
  initialData?: Partial<CreateVaccinationData>;
  suggestions?: string[];
  loading?: boolean;
  onSubmit: (data: CreateVaccinationData) => void;
  onCancel?: () => void;
}

export const VaccineForm: React.FC<VaccineFormProps> = ({
  initialData,
  suggestions = [],
  loading = false,
  onSubmit,
  onCancel
}) => {
  const [formData, setFormData] = useState<CreateVaccinationData>({
    vaccineName: initialData?.vaccineName || '',
    dateAdministered: initialData?.dateAdministered || new Date().toISOString().split('T')[0],
    nextDoseDate: initialData?.nextDoseDate || '',
    veterinarian: initialData?.veterinarian || '',
    clinic: initialData?.clinic || '',
    batchNumber: initialData?.batchNumber || '',
    manufacturer: initialData?.manufacturer || '',
    notes: initialData?.notes || '',
    reminderActive: initialData?.reminderActive ?? true
  });

  const [showSuggestions, setShowSuggestions] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target;
    const checked = (e.target as HTMLInputElement).checked;
    
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSuggestionClick = (suggestion: string) => {
    // If it's a known suggestion, automatically project next dose to 1 year from administered date
    const currentAdministered = formData.dateAdministered || new Date().toISOString().split('T')[0];
    const nextYearDate = new Date(currentAdministered);
    nextYearDate.setFullYear(nextYearDate.getFullYear() + 1);
    
    setFormData(prev => ({ 
      ...prev, 
      vaccineName: suggestion,
      nextDoseDate: nextYearDate.toISOString().split('T')[0]
    }));
    setShowSuggestions(false);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form className="organism-vaccine-form" onSubmit={handleSubmit}>
      <div className="organism-vaccine-form__field organism-vaccine-form__autocomplete">
        <Input
          label="Nome da Vacina *"
          name="vaccineName"
          value={formData.vaccineName}
          onChange={handleChange}
          onFocus={() => setShowSuggestions(true)}
          onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
          placeholder="Ex: Antirrábica"
          required
        />
        {showSuggestions && suggestions.length > 0 && (
          <div className="organism-vaccine-form__suggestions">
            {suggestions.filter(s => s.toLowerCase().includes(formData.vaccineName.toLowerCase())).map(suggestion => (
              <div 
                key={suggestion}
                className="organism-vaccine-form__suggestion-item"
                onClick={() => handleSuggestionClick(suggestion)}
              >
                {suggestion}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="organism-vaccine-form__row">
        <Input
          label="Data da Aplicação *"
          name="dateAdministered"
          type="date"
          value={formData.dateAdministered}
          onChange={handleChange}
          required
        />
        <Input
          label="Próxima Dose"
          name="nextDoseDate"
          type="date"
          value={formData.nextDoseDate}
          onChange={handleChange}
        />
      </div>

      <div className="organism-vaccine-form__row">
        <Input
          label="Veterinário(a)"
          name="veterinarian"
          value={formData.veterinarian}
          onChange={handleChange}
          placeholder="Nome do profissional"
        />
        <Input
          label="Clínica"
          name="clinic"
          value={formData.clinic}
          onChange={handleChange}
          placeholder="Local da aplicação"
        />
      </div>

      <div className="organism-vaccine-form__row">
        <Input
          label="Lote"
          name="batchNumber"
          value={formData.batchNumber}
          onChange={handleChange}
        />
        <Input
          label="Fabricante"
          name="manufacturer"
          value={formData.manufacturer}
          onChange={handleChange}
        />
      </div>

      <div className="organism-vaccine-form__field">
        <label className="atom-input__label">Anotações</label>
        <textarea
          name="notes"
          value={formData.notes}
          onChange={handleChange}
          className="organism-vaccine-form__textarea"
          placeholder="Observações adicionais..."
          rows={3}
        />
      </div>

      <div className="organism-vaccine-form__checkbox">
        <input
          type="checkbox"
          id="reminderActive"
          name="reminderActive"
          checked={formData.reminderActive}
          onChange={handleChange}
        />
        <label htmlFor="reminderActive">Ativar lembrete para próxima dose</label>
      </div>

      <div className="organism-vaccine-form__actions">
        {onCancel && (
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancelar
          </Button>
        )}
        <Button type="submit" disabled={loading}>
          {loading ? 'Salvando...' : 'Salvar Vacina'}
        </Button>
      </div>
    </form>
  );
};
