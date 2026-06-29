import React, { useState, useEffect } from 'react';
import { useAuth } from '../../../contexts/AuthContext';
import { useToast } from '../../molecules/Toast/Toast';
import { FormField } from '../../molecules/FormField/FormField';
import { Button } from '../../atoms/Button/Button';
import './ProfileForm.css';

export const ProfileForm: React.FC = () => {
  const { user, updateProfile, deleteAccount } = useAuth();
  const { showToast } = useToast();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [timezone, setTimezone] = useState('America/Sao_Paulo');

  const [errors, setErrors] = useState<{ name?: string; email?: string; timezone?: string }>({});
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [showConfirmDelete, setShowConfirmDelete] = useState(false);

  // Carrega dados iniciais do usuário logado
  useEffect(() => {
    if (user) {
      setName(user.name);
      setEmail(user.email);
      setAvatarUrl(user.avatarUrl || '');
      setTimezone(user.timezone || 'America/Sao_Paulo');
    }
  }, [user]);

  const validate = () => {
    const newErrors: { name?: string; email?: string; timezone?: string } = {};

    if (!name) {
      newErrors.name = 'O nome é obrigatório.';
    } else if (name.length < 2) {
      newErrors.name = 'O nome deve ter no mínimo 2 caracteres.';
    }

    if (!email) {
      newErrors.email = 'O e-mail é obrigatório.';
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = 'Formato de e-mail inválido.';
    }

    if (!timezone) {
      newErrors.timezone = 'O fuso horário é obrigatório.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setIsSaving(true);
    try {
      await updateProfile(name, email, avatarUrl, timezone);
      showToast('Perfil atualizado com sucesso! ✨', 'success');
    } catch (error: any) {
      console.error(error);
      const message = error.response?.data?.error?.message || 'Erro ao atualizar perfil.';
      showToast(message, 'error');
    } finally {
      setIsSaving(false);
    }
  };

  const handleDelete = async () => {
    setIsDeleting(true);
    try {
      await deleteAccount();
      showToast('Sua conta foi excluída com sucesso.', 'success');
      // Redireciona automaticamente via context/AuthContext
    } catch (error: any) {
      console.error(error);
      showToast('Erro ao excluir conta. Tente novamente.', 'error');
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <div className="profile-container">
      <h2 className="profile-title">Configurações do Perfil</h2>

      <form onSubmit={handleSubmit}>
        <div className="profile-avatar-preview">
          {avatarUrl ? (
            <img src={avatarUrl} alt="Avatar" className="profile-avatar-img" />
          ) : (
            <div className="profile-avatar-placeholder">👤</div>
          )}
          <FormField
            label="URL da Foto do Perfil"
            type="text"
            id="avatarUrl"
            placeholder="https://exemplo.com/sua-foto.jpg"
            value={avatarUrl}
            onChange={(e) => setAvatarUrl(e.target.value)}
          />
        </div>

        <FormField
          label="Nome"
          type="text"
          id="name"
          placeholder="Seu nome"
          value={name}
          onChange={(e) => setName(e.target.value)}
          error={errors.name}
          required
        />

        <FormField
          label="E-mail"
          type="email"
          id="email"
          placeholder="seu-email@petlife.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          error={errors.email}
          required
        />

        <FormField
          label="Fuso Horário"
          type="text"
          id="timezone"
          placeholder="America/Sao_Paulo"
          value={timezone}
          onChange={(e) => setTimezone(e.target.value)}
          error={errors.timezone}
          required
        />

        <Button type="submit" isLoading={isSaving} style={{ width: 'auto', minWidth: '150px' }}>
          Salvar Alterações
        </Button>
      </form>

      <div className="danger-zone">
        <h3 className="danger-zone-title">Zona de Perigo</h3>
        <p className="danger-zone-desc">
          Ao excluir sua conta, todos os seus dados e registros de pets serão excluídos permanentemente de nossos servidores em conformidade com a LGPD. Esta ação não poderá ser desfeita.
        </p>

        {!showConfirmDelete ? (
          <Button variant="danger" onClick={() => setShowConfirmDelete(true)} style={{ width: 'auto' }}>
            Excluir Conta
          </Button>
        ) : (
          <div>
            <p style={{ color: 'var(--error)', fontWeight: 600, marginBottom: '16px', fontSize: '14px' }}>
              ⚠️ Tem certeza absoluta? Essa ação é irreversível.
            </p>
            <div className="profile-actions">
              <Button variant="danger" isLoading={isDeleting} onClick={handleDelete} style={{ width: 'auto' }}>
                Sim, excluir minha conta permanentemente
              </Button>
              <Button variant="secondary" onClick={() => setShowConfirmDelete(false)} style={{ width: 'auto' }}>
                Cancelar
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
