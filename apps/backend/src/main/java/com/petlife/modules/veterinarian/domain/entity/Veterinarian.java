package com.petlife.modules.veterinarian.domain.entity;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.domain.entity.PetSpecies;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
public class Veterinarian {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private User user;
    private String crmvNumber;
    private String crmvState;
    private CrmvStatus crmvStatus = CrmvStatus.PENDING;
    private String fullName;
    private String bio;
    private List<String> specialties = new ArrayList<>();
    private List<PetSpecies> speciesServed = new ArrayList<>();
    private List<Modality> modalities = new ArrayList<>();
    private List<PaymentType> paymentTypes = new ArrayList<>();
    private List<String> insurancePlans = new ArrayList<>();
    private boolean emergencyOnDuty = false;
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.UNAVAILABLE;
    private String profilePhotoUrl;
    private String phone;
    private String websiteUrl;
    private List<VetAddress> addresses = new ArrayList<>();
    private List<VetSchedule> schedules = new ArrayList<>();
    private List<VetFavorite> favorites = new ArrayList<>();
}
