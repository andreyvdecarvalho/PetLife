package com.petlife.modules.pet.entity;

import com.petlife.modules.auth.entity.User;
import com.petlife.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pets")
@Getter
@Setter
public class Pet extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "species", nullable = false)
    private PetSpecies species;

    @Column(name = "breed", length = 100)
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    private PetSex sex;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private PetSize size;

    @Column(name = "neutered", nullable = false)
    private boolean neutered = false;

    @Column(name = "microchip_id", length = 50)
    private String microchipId;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PetStatus status = PetStatus.ACTIVE;
}
