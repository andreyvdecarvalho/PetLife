import type { PetSpecies } from '../shared/Species';
export type PetSex = 'MALE' | 'FEMALE' | 'UNKNOWN';
export type PetSize = 'MINI' | 'SMALL' | 'MEDIUM' | 'LARGE' | 'GIANT';
export type PetStatus = 'ACTIVE' | 'ARCHIVED' | 'DECEASED';

export interface Pet {
  id: string;
  userId: string;
  name: string;
  species: PetSpecies;
  breed?: string;
  sex: PetSex;
  birthDate?: string;
  weightKg?: number;
  size?: PetSize;
  neutered: boolean;
  microchipId?: string;
  allergies?: string;
  notes?: string;
  photoUrl?: string;
  status: PetStatus;
  createdAt: string;
  updatedAt: string;
}
