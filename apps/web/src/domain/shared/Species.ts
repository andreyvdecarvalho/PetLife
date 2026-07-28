export type PetSpecies = 'DOG' | 'CAT' | 'BIRD' | 'FISH' | 'RODENT' | 'REPTILE' | 'EXOTIC' | 'WILDLIFE' | 'OTHER';

export const SPECIES_LABELS: Record<PetSpecies, string> = {
  DOG: 'Cachorro',
  CAT: 'Gato',
  BIRD: 'Pássaro',
  FISH: 'Peixe',
  RODENT: 'Roedor',
  REPTILE: 'Réptil',
  EXOTIC: 'Exótico',
  WILDLIFE: 'Silvestre',
  OTHER: 'Outro'
};
