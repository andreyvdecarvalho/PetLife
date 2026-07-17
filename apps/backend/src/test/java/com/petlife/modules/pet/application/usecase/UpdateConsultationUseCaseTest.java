package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Consultation;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.ConsultationResponse;
import com.petlife.modules.pet.infrastructure.dto.UpdateConsultationRequest;
import com.petlife.modules.auth.entity.User;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateConsultationUseCaseTest {

    @Mock
    private ConsultationRepositoryPort consultationRepositoryPort;

    @Mock
    private PetRepositoryPort petRepositoryPort;

    @InjectMocks
    private UpdateConsultationUseCase updateConsultationUseCase;

    private UUID userId;
    private UUID petId;
    private UUID consultationId;
    private User user;
    private Pet pet;
    private Consultation consultation;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();
        consultationId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        pet = new Pet();
        pet.setId(petId);
        pet.setUser(user);

        consultation = new Consultation();
        consultation.setId(consultationId);
        consultation.setPet(pet);
        consultation.setReason("Old reason");
    }

    @Test
    void shouldUpdateConsultationSuccessfully() {
        UpdateConsultationRequest req = new UpdateConsultationRequest();
        req.setReason("New Reason");
        req.setDiagnosis("New Diagnosis");
        req.setPrescriptions("New Prescriptions");
        req.setNotes("New Notes");
        req.setWeightAtVisit(java.math.BigDecimal.valueOf(10.5));
        req.setFollowUpDate(java.time.LocalDate.now().plusDays(10));

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        ConsultationResponse response = updateConsultationUseCase.execute(petId, consultationId, userId, req);

        assertEquals("New Reason", response.getReason());
        assertEquals("New Diagnosis", response.getDiagnosis());
        assertEquals("New Prescriptions", response.getPrescriptions());
        assertEquals("New Notes", response.getNotes());
        assertEquals(java.math.BigDecimal.valueOf(10.5), response.getWeightAtVisit());
        assertNotNull(response.getFollowUpDate());

        verify(consultationRepositoryPort).save(any());
    }

    @Test
    void shouldUpdateConsultationPartially() {
        UpdateConsultationRequest req = new UpdateConsultationRequest();
        req.setDiagnosis("New Diagnosis Only");

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));
        when(consultationRepositoryPort.save(any())).thenAnswer(i -> i.getArgument(0));

        ConsultationResponse response = updateConsultationUseCase.execute(petId, consultationId, userId, req);

        assertEquals("Old reason", response.getReason()); // unchanged
        assertEquals("New Diagnosis Only", response.getDiagnosis()); // changed

        verify(consultationRepositoryPort).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPetNotFound() {
        UpdateConsultationRequest req = new UpdateConsultationRequest();
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                updateConsultationUseCase.execute(petId, consultationId, userId, req));
    }

    @Test
    void shouldThrowExceptionWhenUserNotOwner() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        pet.setUser(otherUser);

        UpdateConsultationRequest req = new UpdateConsultationRequest();
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));

        assertThrows(BusinessException.class, () ->
                updateConsultationUseCase.execute(petId, consultationId, userId, req));
    }

    @Test
    void shouldThrowExceptionWhenConsultationNotFound() {
        UpdateConsultationRequest req = new UpdateConsultationRequest();
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                updateConsultationUseCase.execute(petId, consultationId, userId, req));
    }

    @Test
    void shouldThrowExceptionWhenConsultationDoesNotBelongToPet() {
        Pet otherPet = new Pet();
        otherPet.setId(UUID.randomUUID());
        consultation.setPet(otherPet);

        UpdateConsultationRequest req = new UpdateConsultationRequest();
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));

        assertThrows(BusinessException.class, () ->
                updateConsultationUseCase.execute(petId, consultationId, userId, req));
    }
}
