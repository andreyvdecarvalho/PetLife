package com.petlife.modules.veterinarian.entity;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.pet.entity.PetSpecies;
import com.petlife.shared.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veterinarians")
@Getter
@Setter
public class Veterinarian extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "crmv_number", nullable = false, unique = true, length = 20)
    private String crmvNumber;

    @Column(name = "crmv_state", nullable = false, length = 2)
    private String crmvState;

    @Enumerated(EnumType.STRING)
    @Column(name = "crmv_status", nullable = false, length = 20)
    private CrmvStatus crmvStatus = CrmvStatus.PENDING;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "specialties")
    private List<String> specialties = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "species_served")
    private List<PetSpecies> speciesServed = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "modalities")
    private List<Modality> modalities = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_types")
    private List<PaymentType> paymentTypes = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "insurance_plans")
    private List<String> insurancePlans = new ArrayList<>();

    @Column(name = "emergency_on_duty", nullable = false)
    private boolean emergencyOnDuty = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false, length = 20)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.UNAVAILABLE;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<VetAddress> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<VetSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<VetFavorite> favorites = new ArrayList<>();
}
