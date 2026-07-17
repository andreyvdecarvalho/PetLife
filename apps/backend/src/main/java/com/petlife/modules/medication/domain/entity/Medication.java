package com.petlife.modules.medication.domain.entity;

import com.petlife.modules.pet.entity.Pet;
import com.petlife.shared.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medication")
@Getter
@Setter
public class Medication extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false, foreignKey = @ForeignKey(name = "fk_medication_pet"))
    private Pet pet;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "dosage", nullable = false, length = 100)
    private String dosage;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 50)
    private MedicationFrequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "medication_type", nullable = false, length = 50)
    private MedicationType medicationType = MedicationType.MEDICINE;

    @Column(name = "custom_frequency_hours")
    private Integer customFrequencyHours;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "times_of_day", columnDefinition = "jsonb", nullable = false)
    private List<String> timesOfDay = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private MedicationStatus status = MedicationStatus.ACTIVE;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MedicationAdministration> administrations = new ArrayList<>();
}
