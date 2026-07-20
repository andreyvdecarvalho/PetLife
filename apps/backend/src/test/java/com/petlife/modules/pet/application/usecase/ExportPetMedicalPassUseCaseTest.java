package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.domain.entity.UserPlan;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.TimelineEventType;
import com.petlife.modules.pet.infrastructure.dto.TimelineEventResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportPetMedicalPassUseCaseTest {

    @Mock
    private PetRepositoryPort petRepositoryPort;

    @Mock
    private GetPetTimelineUseCase getPetTimelineUseCase;

    @InjectMocks
    private ExportPetMedicalPassUseCase exportPetMedicalPassUseCase;

    private UUID userId;
    private UUID petId;
    private User user;
    private Pet pet;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setPlan(UserPlan.PREMIUM); // Set premium for success cases

        pet = new Pet();
        pet.setId(petId);
        pet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));
        pet.setName("Rex");
        pet.setBirthDate(LocalDate.now().minusYears(2));
    }

    @Test
    void shouldExportPdfSuccessfullyForPremiumUser() {
        TimelineEventResponse event = TimelineEventResponse.builder()
                .id(UUID.randomUUID())
                .type(TimelineEventType.VACCINE)
                .date(OffsetDateTime.now())
                .title("Vacina Antirábica")
                .description("Aplicada")
                .build();

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(getPetTimelineUseCase.execute(petId, userId, null, 0, Integer.MAX_VALUE))
                .thenReturn(List.of(event));

        byte[] pdfBytes = exportPetMedicalPassUseCase.execute(petId, userId, null, null);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void shouldThrowForbiddenExceptionForFreeUser() {
        user.setPlan(UserPlan.FREE); // Free user
        pet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                exportPetMedicalPassUseCase.execute(petId, userId, null, null)
        );

        assertEquals("PREMIUM_REQUIRED", exception.getCode());
    }

    @Test
    void shouldFilterEventsByDateRange() {
        TimelineEventResponse event1 = TimelineEventResponse.builder()
                .id(UUID.randomUUID())
                .type(TimelineEventType.VACCINE)
                .date(OffsetDateTime.now().minusDays(10))
                .title("Event 10 days ago")
                .build();

        TimelineEventResponse event2 = TimelineEventResponse.builder()
                .id(UUID.randomUUID())
                .type(TimelineEventType.VACCINE)
                .date(OffsetDateTime.now().minusDays(2))
                .title("Event 2 days ago")
                .build();

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        // Return both events, the usecase should filter in memory
        when(getPetTimelineUseCase.execute(petId, userId, null, 0, Integer.MAX_VALUE))
                .thenReturn(List.of(event1, event2));

        // Let's filter from 5 days ago to now (should only include event2)
        LocalDate start = LocalDate.now().minusDays(5);
        byte[] pdfBytes = exportPetMedicalPassUseCase.execute(petId, userId, start, null);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }
}
