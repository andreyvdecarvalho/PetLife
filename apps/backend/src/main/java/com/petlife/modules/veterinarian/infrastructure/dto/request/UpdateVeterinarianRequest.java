package com.petlife.modules.veterinarian.infrastructure.dto.request;

import com.petlife.modules.pet.domain.entity.PetSpecies;
import com.petlife.modules.veterinarian.domain.entity.Modality;
import com.petlife.modules.veterinarian.domain.entity.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVeterinarianRequest {
    private String fullName;
    private String bio;
    private List<String> specialties;
    private List<PetSpecies> speciesServed;
    private List<Modality> modalities;
    private List<PaymentType> paymentTypes;
    private List<String> insurancePlans;
    private String profilePhotoUrl;
    private String phone;
    private String websiteUrl;
}
