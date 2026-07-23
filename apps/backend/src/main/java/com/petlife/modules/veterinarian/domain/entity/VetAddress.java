package com.petlife.modules.veterinarian.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
public class VetAddress {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Veterinarian veterinarian;
    private String label;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean isPrimary = true;
}
