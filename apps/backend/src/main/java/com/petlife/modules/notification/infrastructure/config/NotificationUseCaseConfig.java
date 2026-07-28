package com.petlife.modules.notification.infrastructure.config;

import com.petlife.modules.medication.application.port.MedicationAdministrationRepositoryPort;
import com.petlife.modules.notification.application.usecase.EnqueueNotificationUseCase;
import com.petlife.modules.notification.application.usecase.ProcessPendingEventsUseCase;
import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.VaccinationPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationUseCaseConfig {

    @Bean
    public ProcessPendingEventsUseCase processPendingEventsUseCase(
            VaccinationPort vaccinationRepository,
            ConsultationRepositoryPort consultationRepository,
            GroomingRepositoryPort groomingRepository,
            MedicationAdministrationRepositoryPort administrationRepository,
            PetRepositoryPort petRepository,
            EnqueueNotificationUseCase enqueueNotificationUseCase) {
        return new ProcessPendingEventsUseCase(
                vaccinationRepository,
                consultationRepository,
                groomingRepository,
                administrationRepository,
                petRepository,
                enqueueNotificationUseCase
        );
    }
}
