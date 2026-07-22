package com.petlife.modules.veterinarian.infrastructure.persistence.entity;

import com.petlife.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "vet_schedules")
@Getter
@Setter
public class VetScheduleJpaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", nullable = false)
    private VeterinarianJpaEntity veterinarian;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private java.time.DayOfWeek dayOfWeek;

    @Column(name = "open_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_active", nullable = false)
    private boolean isAvailable = true;
}
