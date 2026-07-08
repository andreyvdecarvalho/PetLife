import { useState, type FormEvent } from 'react';
import type { GroomingType } from '../../domain/pet/Grooming';
import type { CreateGroomingData } from '../../infrastructure/http/grooming.api';

interface UseGroomingFormProps {
  initialValues?: {
    type: GroomingType;
    date: string;
    provider?: string;
    cost?: number;
    frequencyDays?: number;
    notes?: string;
  };
  onSubmit: (values: CreateGroomingData) => Promise<void>;
}

export function useGroomingForm({ initialValues, onSubmit }: UseGroomingFormProps) {
  const [type, setType] = useState<GroomingType>(initialValues?.type || 'BATH');
  const [date, setDate] = useState<string>(initialValues?.date || new Date().toISOString().split('T')[0]);
  const [provider, setProvider] = useState<string>(initialValues?.provider || '');
  const [cost, setCost] = useState<string>(initialValues?.cost !== undefined ? String(initialValues.cost) : '');
  const [frequencyDays, setFrequencyDays] = useState<string>(
    initialValues?.frequencyDays !== undefined ? String(initialValues.frequencyDays) : ''
  );
  const [notes, setNotes] = useState<string>(initialValues?.notes || '');
  
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validate = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!date) {
      newErrors.date = 'A data é obrigatória.';
    }

    if (!type) {
      newErrors.type = 'O tipo de serviço é obrigatório.';
    }

    if (cost !== '' && isNaN(Number(cost))) {
      newErrors.cost = 'O custo deve ser um número válido.';
    } else if (cost !== '' && Number(cost) < 0) {
      newErrors.cost = 'O custo não pode ser negativo.';
    }

    if (frequencyDays !== '' && (isNaN(Number(frequencyDays)) || !Number.isInteger(Number(frequencyDays)))) {
      newErrors.frequencyDays = 'A periodicidade deve ser um número de dias inteiro.';
    } else if (frequencyDays !== '' && Number(frequencyDays) <= 0) {
      newErrors.frequencyDays = 'A periodicidade deve ser maior que 0 dias.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setIsSubmitting(true);
    try {
      const data: CreateGroomingData = {
        type,
        date,
        provider: provider || undefined,
        cost: cost !== '' ? Number(cost) : undefined,
        frequencyDays: frequencyDays !== '' ? Number(frequencyDays) : undefined,
        notes: notes || undefined,
      };
      await onSubmit(data);
    } finally {
      setIsSubmitting(false);
    }
  };

  return {
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
  };
}
