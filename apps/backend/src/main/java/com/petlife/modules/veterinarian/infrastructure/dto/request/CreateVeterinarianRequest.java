package com.petlife.modules.veterinarian.infrastructure.dto.request;

import com.petlife.modules.pet.domain.entity.PetSpecies;
import com.petlife.modules.veterinarian.domain.entity.Modality;
import com.petlife.modules.veterinarian.domain.entity.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateVeterinarianRequest {

    @NotBlank
    @Pattern(regexp = "^CRMV-[A-Z]{2}-\\d{1,6}$", message = "Invalid CRMV format. Expected CRMV-UF-NNNNN")
    private String crmvNumber;

    @NotBlank
    @Size(min = 2, max = 2)
    private String crmvState;

    @NotBlank
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
