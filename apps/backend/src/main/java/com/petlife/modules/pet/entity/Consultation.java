package com.petlife.modules.pet.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "consultation")
@Getter
@Setter
public class Consultation {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false, foreignKey = @ForeignKey(name = "fk_consultation_pet"))
    private Pet pet;

    @Column(name = "date", nullable = false)
    private OffsetDateTime date;

    @Column(name = "veterinarian", length = 200)
    private String veterinarian;

    @Column(name = "clinic", length = 200)
    private String clinic;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "prescriptions", columnDefinition = "TEXT")
    private String prescriptions;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "weight_at_visit", precision = 5, scale = 2)
    private BigDecimal weightAtVisit;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attachments", columnDefinition = "jsonb")
    private List<String> attachments = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
