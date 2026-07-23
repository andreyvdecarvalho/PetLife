package com.petlife.modules.medication.infrastructure.persistence.entity;

import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "medication_administration")
@Getter
@Setter
public class MedicationAdministrationJpaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_medication_administration_medication"))
    private MedicationJpaEntity medication;

    @Column(name = "scheduled_time", nullable = false)
    private OffsetDateTime scheduledTime;

    @Column(name = "administered_at")
    private OffsetDateTime administeredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private MedicationAdministrationStatus status = MedicationAdministrationStatus.PENDING;

    @Column(name = "skipped_reason", length = 255)
    private String skippedReason;
}
