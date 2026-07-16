import { useState } from 'react';
import { petApi } from '../../infrastructure/http/pet.api';

export const useDeletePet = () => {
  const [isDeleting, setIsDeleting] = useState(false);

  const deletePet = async (petId: string) => {
    setIsDeleting(true);
    try {
      await petApi.delete(petId);
    } finally {
      setIsDeleting(false);
    }
  };

  return { deletePet, isDeleting };
};
