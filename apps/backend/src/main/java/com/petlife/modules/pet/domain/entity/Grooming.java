package com.petlife.modules.pet.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Grooming {

    private UUID id;
    private Pet pet;
    private GroomingType type;
    private LocalDate date;
    private String provider;
    private BigDecimal cost;
    private Integer frequencyDays;
    private LocalDate nextDate;
    private String notes;
    private List<String> photos = new ArrayList<>();
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public void calculateNextDate() {
        if (this.frequencyDays != null && this.frequencyDays > 0 && this.date != null) {
            this.nextDate = this.date.plusDays(this.frequencyDays);
        } else {
            this.nextDate = null;
        }
    }
}
