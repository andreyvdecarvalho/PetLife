package com.petlife.modules.veterinarian.infrastructure.dto.response;

import com.petlife.modules.pet.entity.PetSpecies;
import com.petlife.modules.veterinarian.entity.AvailabilityStatus;
import com.petlife.modules.veterinarian.entity.CrmvStatus;
import com.petlife.modules.veterinarian.entity.Modality;
import com.petlife.modules.veterinarian.entity.PaymentType;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class VeterinarianResponse {
    private UUID id;
    private String crmvNumber;
    private String crmvState;
    private CrmvStatus crmvStatus;
    private String fullName;
    private String bio;
    private List<String> specialties;
    private List<PetSpecies> speciesServed;
    private List<Modality> modalities;
    private List<PaymentType> paymentTypes;
    private List<String> insurancePlans;
    private boolean emergencyOnDuty;
    private AvailabilityStatus availabilityStatus;
    private String profilePhotoUrl;
    private String phone;
    private String websiteUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static VeterinarianResponse fromEntity(Veterinarian vet) {
        return VeterinarianResponse.builder()
                .id(vet.getId())
                .crmvNumber(vet.getCrmvNumber())
                .crmvState(vet.getCrmvState())
                .crmvStatus(vet.getCrmvStatus())
                .fullName(vet.getFullName())
                .bio(vet.getBio())
                .specialties(vet.getSpecialties())
                .speciesServed(vet.getSpeciesServed())
                .modalities(vet.getModalities())
                .paymentTypes(vet.getPaymentTypes())
                .insurancePlans(vet.getInsurancePlans())
                .emergencyOnDuty(vet.isEmergencyOnDuty())
                .availabilityStatus(vet.getAvailabilityStatus())
                .profilePhotoUrl(vet.getProfilePhotoUrl())
                .phone(vet.getPhone())
                .websiteUrl(vet.getWebsiteUrl())
                .createdAt(vet.getCreatedAt())
                .updatedAt(vet.getUpdatedAt())
                .build();
    }
}
