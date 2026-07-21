package com.petlife.shared.factories;

import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.medication.domain.entity.*;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class MedicationFactory {

    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

    public static Medication makeMedication(Pet pet) {
        return makeMedication(pet, m -> {});
    }

    public static Medication makeMedication(Pet pet, Consumer<Medication> overrides) {
        Medication medication = new Medication();
        var petJpa = new com.petlife.modules.pet.infrastructure.persistence.entity.PetJpaEntity();
        petJpa.setId(pet.getId());
        
        if (pet.getUser() != null) {
            var userJpa = new com.petlife.modules.auth.infrastructure.persistence.entity.UserJpaEntity();
            userJpa.setId(pet.getUser().getId());
            petJpa.setUser(userJpa);
        }
        
        medication.setPetEntity(petJpa);
        medication.setName(faker.lorem().word());
        medication.setDosage("1 comprimido");
        medication.setFrequency(MedicationFrequency.DAILY);
        medication.setStartDate(LocalDate.now());
        medication.setEndDate(LocalDate.now().plusDays(7));
        medication.setTimesOfDay(List.of("08:00", "20:00"));
        medication.setStatus(MedicationStatus.ACTIVE);
        medication.setCreatedAt(LocalDateTime.now());
        medication.setUpdatedAt(LocalDateTime.now());
        overrides.accept(medication);
        return medication;
    }

    public static MedicationAdministration makeAdministration(Medication medication) {
        return makeAdministration(medication, a -> {});
    }

    public static MedicationAdministration makeAdministration(Medication medication, Consumer<MedicationAdministration> overrides) {
        MedicationAdministration administration = new MedicationAdministration();
        administration.setMedication(medication);
        administration.setScheduledTime(OffsetDateTime.now(ZoneOffset.UTC).plusHours(2));
        administration.setStatus(MedicationAdministrationStatus.PENDING);
        administration.setCreatedAt(LocalDateTime.now());
        administration.setUpdatedAt(LocalDateTime.now());
        overrides.accept(administration);
        return administration;
    }
}
