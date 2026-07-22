package com.petlife.modules.veterinarian.infrastructure.persistence.entity;

import com.petlife.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "vet_addresses")
@Getter
@Setter
public class VetAddressJpaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", nullable = false)
    private VeterinarianJpaEntity veterinarian;

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    @Column(name = "street", length = 255)
    private String street;

    @Column(name = "number", length = 50)
    private String number;

    @Column(name = "complement", length = 255)
    private String complement;

    @Column(name = "neighborhood", length = 255)
    private String neighborhood;

    @Column(name = "city", length = 255)
    private String city;

    @Column(name = "state", length = 2)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = true;
}
