package com.petlife.modules.pet.infrastructure.persistence.entity;

import com.petlife.modules.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.petlife.modules.pet.domain.entity.PetSex;
import com.petlife.modules.pet.domain.entity.PetSize;
import com.petlife.modules.pet.domain.entity.PetSpecies;
import com.petlife.modules.pet.domain.entity.PetStatus;
import com.petlife.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pets")
@Getter
@Setter
public class PetJpaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "species", nullable = false)
    private PetSpecies species;

    @Column(name = "breed", length = 100)
    private String breed;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "sex", nullable = false)
    private PetSex sex;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
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

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private PetStatus status = PetStatus.ACTIVE;
}
