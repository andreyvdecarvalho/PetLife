import React, { useEffect, useState } from 'react';
import { useNotificationPreferences } from '../../../application/notification/useNotificationPreferences';
import { useToast } from '../../molecules/Toast';
import { Button } from '../../atoms/Button';
import './styles.css';

export const NotificationPreferencesForm: React.FC = () => {
  const { preferences, isLoading, isSaving, fetchPreferences, savePreferences } =
    useNotificationPreferences();
  const { showToast } = useToast();

  const [pushEnabled, setPushEnabled] = useState(true);
  const [emailEnabled, setEmailEnabled] = useState(true);
  const [vaccines, setVaccines] = useState(true);
  const [medications, setMedications] = useState(true);
  const [appointments, setAppointments] = useState(true);
  const [grooming, setGrooming] = useState(true);
  const [marketing, setMarketing] = useState(false);
  const [dndStart, setDndStart] = useState('22:00');
  const [dndEnd, setDndEnd] = useState('07:00');

  useEffect(() => {
    fetchPreferences();
  }, [fetchPreferences]);

  useEffect(() => {
    if (preferences) {
      setPushEnabled(preferences.pushEnabled);
      setEmailEnabled(preferences.emailEnabled);
      setVaccines(preferences.vaccines);
      setMedications(preferences.medications);
      setAppointments(preferences.appointments);
      setGrooming(preferences.grooming);
      setMarketing(preferences.marketing);
      setDndStart(preferences.doNotDisturbStart.substring(0, 5));
      setDndEnd(preferences.doNotDisturbEnd.substring(0, 5));
    }
  }, [preferences]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const success = await savePreferences({
      pushEnabled,
      emailEnabled,
      vaccines,
      medications,
      appointments,
      grooming,
      marketing,
      doNotDisturbStart: `${dndStart}:00`,
      doNotDisturbEnd: `${dndEnd}:00`,
    });
    if (success) {
      showToast('Preferências de notificação salvas com sucesso! 🔔', 'success');
    } else {
      showToast('Erro ao salvar preferências de notificação.', 'error');
    }
  };

  if (isLoading) {
    return <div className="preferences-loading">Carregando configurações...</div>;
  }

  const renderToggle = (
    label: string,
    value: boolean,
    setter: (val: boolean) => void,
    id: string
  ) => (
    <div className="preferences-toggle">
      <label htmlFor={id} className="preferences-toggle__label">
        {label}
      </label>
      <input
        type="checkbox"
        id={id}
        checked={value}
        onChange={(e) => setter(e.target.checked)}
        className="preferences-toggle__checkbox"
      />
    </div>
  );

  return (
    <form className="preferences-form" onSubmit={handleSubmit}>
      <h3 className="preferences-section-title">Canais de Notificação</h3>
      {renderToggle('Notificações Push', pushEnabled, setPushEnabled, 'pushEnabled')}
      {renderToggle('Notificações por E-mail', emailEnabled, setEmailEnabled, 'emailEnabled')}

      <h3 className="preferences-section-title">Categorias Silenciáveis</h3>
      {renderToggle('Vacinas', vaccines, setVaccines, 'vaccines')}
      {renderToggle('Medicamentos', medications, setMedications, 'medications')}
      {renderToggle('Consultas & Retornos', appointments, setAppointments, 'appointments')}
      {renderToggle('Banho & Tosa', grooming, setGrooming, 'grooming')}
      {renderToggle('Promoções e Marketing', marketing, setMarketing, 'marketing')}

      <h3 className="preferences-section-title">Período de Silêncio (Não Perturbe)</h3>
      <div className="preferences-time-inputs">
        <div className="preferences-time-field">
          <label htmlFor="dndStart">Início</label>
          <input
            type="time"
            id="dndStart"
            value={dndStart}
            onChange={(e) => setDndStart(e.target.value)}
          />
        </div>
        <div className="preferences-time-field">
          <label htmlFor="dndEnd">Fim</label>
          <input
            type="time"
            id="dndEnd"
            value={dndEnd}
            onChange={(e) => setDndEnd(e.target.value)}
          />
        </div>
      </div>

      <Button
        type="submit"
        isLoading={isSaving}
        style={{ marginTop: '24px', width: 'auto', minWidth: '150px' }}
      >
        Salvar Preferências
      </Button>
    </form>
  );
};
