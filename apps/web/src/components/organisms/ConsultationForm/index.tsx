import React, { useState } from 'react';
import { useConsultations } from '../../../application/consultation/useConsultations';
import { FormField } from '../../molecules/FormField';
import { AttachmentManager } from '../../molecules/AttachmentManager';
import { Button } from '../../atoms/Button';
import { useToast } from '../../molecules/Toast';
import './styles.css';

interface ConsultationFormProps {
  petId: string;
  onSuccess: () => void;
  onCancel: () => void;
}

export const ConsultationForm: React.FC<ConsultationFormProps> = ({ petId, onSuccess, onCancel }) => {
  const { addConsultation, uploadAttachments } = useConsultations(petId);
  const { showToast } = useToast();
  const [loading, setLoading] = useState(false);
  const [files, setFiles] = useState<File[]>([]);
  const [errors, setErrors] = useState<Record<string, string>>({});

  const [formData, setFormData] = useState({
    date: new Date().toISOString().substring(0, 16),
    reason: '',
    veterinarian: '',
    clinic: '',
    diagnosis: '',
    prescriptions: '',
    notes: '',
    weightAtVisit: '',
    followUpDate: '',
    cost: '',
  });

  const handleChange = (field: keyof typeof formData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) setErrors(prev => ({ ...prev, [field]: '' }));
  };

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};
    if (!formData.reason.trim()) newErrors.reason = 'O motivo é obrigatório.';
    else if (formData.reason.length < 3) newErrors.reason = 'O motivo deve ter no mínimo 3 caracteres.';
    if (!formData.date) newErrors.date = 'A data é obrigatória.';
    if (formData.followUpDate && formData.date) {
      const consultDate = new Date(formData.date).toISOString().substring(0, 10);
      if (formData.followUpDate < consultDate) {
        newErrors.followUpDate = 'A data de retorno não pode ser anterior à data da consulta.';
      }
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      const created = await addConsultation({
        date: new Date(formData.date).toISOString(),
        reason: formData.reason,
        veterinarian: formData.veterinarian || undefined,
        clinic: formData.clinic || undefined,
        diagnosis: formData.diagnosis || undefined,
        prescriptions: formData.prescriptions || undefined,
        notes: formData.notes || undefined,
        weightAtVisit: formData.weightAtVisit ? Number(formData.weightAtVisit) : undefined,
        followUpDate: formData.followUpDate || undefined,
        cost: formData.cost ? Number(formData.cost) : undefined,
      });

      if (created) {
        if (files.length > 0) {
          const uploadSuccess = await uploadAttachments(created.id, files);
          if (!uploadSuccess) showToast('Consulta salva, mas falhou ao enviar anexos.', 'error');
        }
        showToast('Consulta médica registrada com sucesso!', 'success');
        onSuccess();
      } else {
        showToast('Erro ao registrar consulta.', 'error');
      }
    } catch (err) {
      showToast('Erro de conexão.', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className="organism-consultation-form" onSubmit={handleSubmit} data-testid="consultation-form">
      <div className="organism-consultation-form__grid">
        <FormField label="Motivo da Consulta" id="c-reason" value={formData.reason} onChange={e => handleChange('reason', e.target.value)} error={errors.reason} required placeholder="Ex: Vacina, Rotina, Sintomas" data-testid="input-motivo-consulta" />
        <FormField label="Data e Hora" id="c-date" type="datetime-local" value={formData.date} onChange={e => handleChange('date', e.target.value)} error={errors.date} required data-testid="input-data-consulta" />
        <FormField label="Veterinário(a)" id="c-vet" value={formData.veterinarian} onChange={e => handleChange('veterinarian', e.target.value)} placeholder="Ex: Dr. Roberto" data-testid="input-vet-consulta" />
        <FormField label="Clínica" id="c-clinic" value={formData.clinic} onChange={e => handleChange('clinic', e.target.value)} placeholder="Ex: Hospital Vet Care" data-testid="input-clinica-consulta" />
        <FormField label="Peso do Pet (kg)" id="c-weight" type="number" step="0.01" value={formData.weightAtVisit} onChange={e => handleChange('weightAtVisit', e.target.value)} placeholder="Ex: 12.5" data-testid="input-peso-consulta" />
        <FormField label="Custo (R$)" id="c-cost" type="number" step="0.01" value={formData.cost} onChange={e => handleChange('cost', e.target.value)} placeholder="Ex: 180.00" data-testid="input-custo-consulta" />
        <FormField label="Data de Retorno" id="c-followup" type="date" value={formData.followUpDate} onChange={e => handleChange('followUpDate', e.target.value)} error={errors.followUpDate} data-testid="input-retorno-consulta" />
      </div>

      <div className="molecule-form-field">
        <label htmlFor="c-diag" className="atom-label">Diagnóstico</label>
        <textarea id="c-diag" value={formData.diagnosis} onChange={e => handleChange('diagnosis', e.target.value)} className="atom-input atom-input--textarea" placeholder="Diagnóstico médico do pet" rows={2} data-testid="input-diag-consulta" />
      </div>

      <div className="molecule-form-field">
        <label htmlFor="c-pres" className="atom-label">Prescrições / Medicamentos</label>
        <textarea id="c-pres" value={formData.prescriptions} onChange={e => handleChange('prescriptions', e.target.value)} className="atom-input atom-input--textarea" placeholder="Receitas e tratamentos prescritos" rows={2} data-testid="input-pres-consulta" />
      </div>

      <div className="molecule-form-field">
        <label htmlFor="c-notes" className="atom-label">Observações</label>
        <textarea id="c-notes" value={formData.notes} onChange={e => handleChange('notes', e.target.value)} className="atom-input atom-input--textarea" placeholder="Notas adicionais" rows={2} data-testid="input-notes-consulta" />
      </div>

      <AttachmentManager files={files} onFilesChange={setFiles} />

      <div className="organism-consultation-form__actions">
        <Button type="button" variant="secondary" onClick={onCancel} disabled={loading} data-testid="btn-cancelar-consulta">Cancelar</Button>
        <Button type="submit" disabled={loading} data-testid="btn-salvar-consulta">{loading ? 'Salvando...' : 'Registrar Consulta'}</Button>
      </div>
    </form>
  );
};
