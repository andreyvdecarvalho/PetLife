package com.petlife.modules.notification.infrastructure.scheduler;

import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.infrastructure.persistence.MedicationAdministrationJpaRepository;
import com.petlife.modules.notification.application.usecase.EnqueueNotificationUseCase;
import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.modules.notification.infrastructure.dto.NotificationPayload;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.entity.Grooming;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.Vaccination;
import com.petlife.modules.pet.infrastructure.persistence.ConsultationJpaRepository;
import com.petlife.modules.pet.infrastructure.persistence.JpaGroomingRepository;
import com.petlife.modules.pet.infrastructure.persistence.PetJpaRepository;
import com.petlife.modules.pet.infrastructure.persistence.VaccinationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final VaccinationRepository vaccinationRepository;
    private final ConsultationJpaRepository consultationRepository;
    private final JpaGroomingRepository groomingRepository;
    private final MedicationAdministrationJpaRepository administrationRepository;
    private final PetJpaRepository petRepository;
    private final EnqueueNotificationUseCase enqueueNotificationUseCase;

    @Scheduled(cron = "0 0 * * * *")
    public void checkUpcomingEvents() {
        log.info("Running scheduled check for upcoming events");
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Vaccination> vaccines = vaccinationRepository.findByReminderActiveTrueAndNextDoseDate(tomorrow);
        for (Vaccination vac : vaccines) {
            if (vac.getPet() != null && vac.getPet().getUser() != null) {
                NotificationPayload payload = new NotificationPayload(
                        vac.getPet().getUser().getId(),
                        NotificationType.VACCINATION_DUE,
                        "Lembrete de Vacina",
                        "A vacina " + vac.getVaccineName() + " para o pet " 
                                + vac.getPet().getName() + " está agendada para amanhã.",
                        vac.getId()
                );
                enqueueNotificationUseCase.execute(payload);
            }
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime endOfTomorrow = now.plusDays(1);
        List<Consultation> consultations = consultationRepository.findByDateBetween(now, endOfTomorrow);
        for (Consultation con : consultations) {
            if (con.getPet() != null && con.getPet().getUser() != null) {
                NotificationPayload payload = new NotificationPayload(
                        con.getPet().getUser().getId(),
                        NotificationType.CONSULTATION_REMINDER,
                        "Lembrete de Consulta",
                        "A consulta do pet " + con.getPet().getName() + " é amanhã.",
                        con.getId()
                );
                enqueueNotificationUseCase.execute(payload);
            }
        }

        List<Consultation> followUps = consultationRepository.findByFollowUpDate(tomorrow);
        for (Consultation con : followUps) {
            if (con.getPet() != null && con.getPet().getUser() != null) {
                NotificationPayload payload = new NotificationPayload(
                        con.getPet().getUser().getId(),
                        NotificationType.CONSULTATION_FOLLOWUP,
                        "Retorno de Consulta",
                        "O retorno da consulta do pet " + con.getPet().getName() + " está agendado para amanhã.",
                        con.getId()
                );
                enqueueNotificationUseCase.execute(payload);
            }
        }

        List<Grooming> groomings = groomingRepository.findByNextDate(tomorrow);
        for (Grooming gro : groomings) {
            if (gro.getPet() != null && gro.getPet().getUser() != null) {
                NotificationPayload payload = new NotificationPayload(
                        gro.getPet().getUser().getId(),
                        NotificationType.GROOMING_REMINDER,
                        "Lembrete de Banho & Tosa",
                        "O banho/tosa do pet " + gro.getPet().getName() + " está agendada para amanhã.",
                        gro.getId()
                );
                enqueueNotificationUseCase.execute(payload);
            }
        }
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void checkLateMedications() {
        log.info("Running scheduled check for late medications");
        OffsetDateTime now = OffsetDateTime.now();
        List<MedicationAdministration> lateAdmin = administrationRepository
                .findByStatusAndScheduledTimeBefore(MedicationAdministrationStatus.PENDING, now);

        for (MedicationAdministration admin : lateAdmin) {
            Medication med = admin.getMedication();
            if (med != null && med.getPet() != null && med.getPet().getUser() != null) {
                NotificationPayload payload = new NotificationPayload(
                        med.getPet().getUser().getId(),
                        NotificationType.MEDICATION_LATE,
                        "Medicamento Atrasado",
                        "A dose do medicamento " + med.getName() + " para o pet " 
                                + med.getPet().getName() + " está atrasada.",
                        admin.getId()
                );
                enqueueNotificationUseCase.execute(payload);
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void checkPetBirthdays() {
        log.info("Running scheduled check for pet birthdays");
        LocalDate today = LocalDate.now();
        List<Pet> pets = petRepository.findPetsByBirthday(today.getMonthValue(), today.getDayOfMonth());

        for (Pet pet : pets) {
            if (pet.getUser() != null) {
                NotificationPayload payload = new NotificationPayload(
                        pet.getUser().getId(),
                        NotificationType.PET_BIRTHDAY,
                        "Feliz Aniversário!",
                        "Parabéns para o pet " + pet.getName() + " pelo seu aniversário hoje!",
                        pet.getId()
                );
                enqueueNotificationUseCase.execute(payload);
            }
        }
    }
}
