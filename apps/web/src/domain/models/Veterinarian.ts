export type CrmvStatus = 'PENDING' | 'VERIFIED' | 'REJECTED';
export type AvailabilityStatus = 'AVAILABLE' | 'UNAVAILABLE';
export type Modality = 'CLINIC' | 'INDIVIDUAL' | 'HOME_VISIT';
export type PaymentType = 'PRIVATE' | 'INSURANCE';
export type PetSpecies = 'DOG' | 'CAT' | 'BIRD' | 'FISH' | 'RODENT' | 'REPTILE' | 'EXOTIC' | 'WILDLIFE' | 'OTHER';

export interface Veterinarian {
  id: string;
  crmvNumber: string;
  crmvState: string;
  crmvStatus: CrmvStatus;
  fullName: string;
  bio?: string;
  specialties: string[];
  speciesServed: PetSpecies[];
  modalities: Modality[];
  paymentTypes: PaymentType[];
  insurancePlans: string[];
  emergencyOnDuty: boolean;
  availabilityStatus: AvailabilityStatus;
  profilePhotoUrl?: string;
  phone?: string;
  websiteUrl?: string;
  createdAt: string;
  updatedAt: string;
}
