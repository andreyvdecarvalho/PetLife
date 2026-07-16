import React, { useState, useEffect } from 'react';
import { useAuth } from '../../../contexts/AuthContext';
import { useToast } from '../../molecules/Toast';
import { FormField } from '../../molecules/FormField';
import { UploadButton } from '../../molecules/UploadButton';
import { Button } from '../../atoms/Button';
import { compressImage } from '../../../utils/imageCompressor';
import './styles.css';

export const ProfileForm: React.FC = () => {
  const { user, updateProfile, deleteAccount } = useAuth();
  const { showToast } = useToast();

  const [name, setName] = useState('');
  const [nickname, setNickname] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [timezone, setTimezone] = useState('AMERICA_SAO_PAULO');

  const [photoFile, setPhotoFile] = useState<File | null>(null);
  const [photoPreview, setPhotoPreview] = useState<string | null>(null);
  const [compressing, setCompressing] = useState(false);

  const [errors, setErrors] = useState<{ name?: string; email?: string; timezone?: string; photo?: string }>({});
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [showConfirmDelete, setShowConfirmDelete] = useState(false);

  // Carrega dados iniciais do usuário logado
  useEffect(() => {
    if (user) {
      setName(user.name);
      setNickname(user.nickname || '');
      setEmail(user.email);
      setPhone(user.phone || '');
      setAvatarUrl(user.avatarUrl || '');
      if (user.avatarUrl) {
        setPhotoPreview(user.avatarUrl);
      }
      setTimezone(user.timezone || 'AMERICA_SAO_PAULO');
    }
  }, [user]);

  const validate = () => {
    const newErrors: { name?: string; email?: string; timezone?: string; photo?: string } = {};

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

  const handlePhotoChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setCompressing(true);
    try {
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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

    setIsSaving(true);
    try {
      await updateProfile({
        name,
        nickname: nickname.trim() || undefined,
        email,
        phone: phone.trim() || undefined,
        avatarUrl,
        timezone,
      }, photoFile || undefined);
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
        <UploadButton
          photoPreview={photoPreview}
          compressing={compressing}
          error={errors.photo}
          onPhotoChange={handlePhotoChange}
        />

        <div style={{ display: 'grid', gap: '16px', gridTemplateColumns: '1fr 1fr' }}>
          <FormField
            label="Nome Completo de Cadastro"
            type="text"
            id="name"
            placeholder="Seu nome"
            value={name}
            onChange={(e) => setName(e.target.value)}
            error={errors.name}
            required
          />

          <FormField
            label="Apelido (como gostaria de ser chamado)"
            type="text"
            id="nickname"
            placeholder="Seu apelido"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
          />
        </div>

        <div style={{ display: 'grid', gap: '16px', gridTemplateColumns: '1fr 1fr' }}>
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
            label="Telefone (opcional)"
            type="text"
            id="phone"
            placeholder="(11) 99999-9999"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
          />
        </div>


        <div className="molecule-form-field">
          <label htmlFor="timezone" className="atom-label atom-label--required">
            Fuso Horário
          </label>
          <select
            id="timezone"
            value={timezone}
            onChange={e => setTimezone(e.target.value)}
            className={`atom-input ${errors.timezone ? 'atom-input--error' : ''}`}
          >
            <option value="AMERICA_SAO_PAULO">Brasília (BRT/BRST)</option>
            <option value="AMERICA_MANAUS">Manaus (AMT)</option>
            <option value="AMERICA_BELEM">Belém (BRT)</option>
            <option value="AMERICA_FORTALEZA">Fortaleza (BRT)</option>
            <option value="AMERICA_RECIFE">Recife (BRT)</option>
            <option value="AMERICA_CUIABA">Cuiabá (AMT)</option>
            <option value="AMERICA_CAMPO_GRANDE">Campo Grande (AMT)</option>
            <option value="AMERICA_RIO_BRANCO">Rio Branco (ACT)</option>
          </select>
          {errors.timezone && <span className="molecule-form-field__error">{errors.timezone}</span>}
        </div>

        <Button type="submit" isLoading={isSaving} disabled={compressing} style={{ width: 'auto', minWidth: '150px' }}>
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
