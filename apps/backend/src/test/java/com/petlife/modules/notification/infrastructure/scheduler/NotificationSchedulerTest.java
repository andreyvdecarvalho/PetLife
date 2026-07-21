package com.petlife.modules.notification.infrastructure.scheduler;

import com.petlife.modules.medication.domain.entity.Medication;
import com.petlife.modules.medication.domain.entity.MedicationAdministration;
import com.petlife.modules.medication.domain.entity.MedicationAdministrationStatus;
import com.petlife.modules.medication.infrastructure.persistence.MedicationAdministrationJpaRepository;
import com.petlife.modules.notification.application.usecase.EnqueueNotificationUseCase;
import com.petlife.modules.notification.domain.entity.NotificationType;
import com.petlife.modules.notification.infrastructure.dto.NotificationPayload;
import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.domain.entity.Consultation;
import com.petlife.modules.pet.infrastructure.persistence.JpaGroomingRepository;
import com.petlife.modules.pet.domain.entity.Grooming;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.Vaccination;
import com.petlife.modules.auth.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest {

    @Mock
    private VaccinationPort vaccinationRepository;

    @Mock
    private ConsultationRepositoryPort consultationRepository;

    @Mock
    private GroomingRepositoryPort groomingRepository;

    @Mock
    private MedicationAdministrationJpaRepository administrationRepository;

    @Mock
    private PetRepositoryPort petRepository;
    @Mock
    private EnqueueNotificationUseCase enqueueNotificationUseCase;

    @InjectMocks
    private NotificationScheduler notificationScheduler;

    private User user;
    private Pet pet;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        pet = new Pet();
        pet.setId(UUID.randomUUID());
        pet.setName("Rex");
        pet.setUser(user);
    }

    @Test
    @DisplayName("checkUpcomingEvents deve buscar e enfileirar alertas de vacina, consulta e banho")
    void checkUpcomingEventsShouldEnqueueNotifications() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Vaccination vac = new Vaccination();
        vac.setId(UUID.randomUUID());
        vac.setVaccineName("Antirrabica");
        vac.setPet(pet);

        Consultation con = new Consultation();
        con.setId(UUID.randomUUID());
        con.setPet(pet);
        con.setDate(OffsetDateTime.now().plusDays(1));

        Consultation followUpCon = new Consultation();
        followUpCon.setId(UUID.randomUUID());
        followUpCon.setPet(pet);
        followUpCon.setFollowUpDate(tomorrow);

        Grooming gro = new Grooming();
        gro.setId(UUID.randomUUID());
        gro.setPet(pet);
        gro.setNextDate(tomorrow);

        given(vaccinationRepository.findByReminderActiveTrueAndNextDoseDate(tomorrow)).willReturn(List.of(vac));
        given(consultationRepository.findByDateBetween(any(), any())).willReturn(List.of(con));
        given(consultationRepository.findByFollowUpDate(tomorrow)).willReturn(List.of(followUpCon));
        given(groomingRepository.findByNextDate(tomorrow)).willReturn(List.of(gro));

        notificationScheduler.checkUpcomingEvents();

        ArgumentCaptor<NotificationPayload> captor = ArgumentCaptor.forClass(NotificationPayload.class);
        verify(enqueueNotificationUseCase, org.mockito.Mockito.times(4)).execute(captor.capture());

        List<NotificationPayload> payloads = captor.getAllValues();
        assertThat(payloads).hasSize(4);
        assertThat(payloads.stream().map(payload -> payload.type()))
                .containsExactlyInAnyOrder(
                        NotificationType.VACCINATION_DUE,
                        NotificationType.CONSULTATION_REMINDER,
                        NotificationType.CONSULTATION_FOLLOWUP,
                        NotificationType.GROOMING_REMINDER
                );
    }

    @Test
    @DisplayName("checkLateMedications deve enfileirar alertas para medicamentos atrasados")
    void checkLateMedicationsShouldEnqueueNotifications() {
        Medication med = new Medication();
        med.setName("Antibiotico");
        var petJpa = new com.petlife.modules.pet.infrastructure.persistence.entity.PetJpaEntity();
        petJpa.setId(pet.getId());
        // Map user to userJpaEntity
        var userJpa = new com.petlife.modules.auth.infrastructure.persistence.entity.UserJpaEntity();
        userJpa.setId(pet.getUser().getId());
        petJpa.setUser(userJpa);
        petJpa.setName(pet.getName());
        med.setPetEntity(petJpa);

        MedicationAdministration admin = new MedicationAdministration();
        admin.setId(UUID.randomUUID());
        admin.setMedication(med);

        given(administrationRepository.findByStatusAndScheduledTimeBefore(eq(MedicationAdministrationStatus.PENDING), any()))
                .willReturn(List.of(admin));

        notificationScheduler.checkLateMedications();

        ArgumentCaptor<NotificationPayload> captor = ArgumentCaptor.forClass(NotificationPayload.class);
        verify(enqueueNotificationUseCase).execute(captor.capture());

        NotificationPayload payload = captor.getValue();
        assertThat(payload.type()).isEqualTo(NotificationType.MEDICATION_LATE);
        assertThat(payload.title()).isEqualTo("Medicamento Atrasado");
    }

    @Test
    @DisplayName("checkPetBirthdays deve enfileirar alertas de parabens no aniversario do pet")
    void checkPetBirthdaysShouldEnqueueNotifications() {
        LocalDate today = LocalDate.now();
        given(petRepository.findPetsByBirthday(today.getMonthValue(), today.getDayOfMonth()))
                .willReturn(List.of(pet));

        notificationScheduler.checkPetBirthdays();

        ArgumentCaptor<NotificationPayload> captor = ArgumentCaptor.forClass(NotificationPayload.class);
        verify(enqueueNotificationUseCase).execute(captor.capture());

        NotificationPayload payload = captor.getValue();
        assertThat(payload.type()).isEqualTo(NotificationType.PET_BIRTHDAY);
        assertThat(payload.title()).isEqualTo("Feliz Aniversário!");
    }
}

