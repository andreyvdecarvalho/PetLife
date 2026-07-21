package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.ConsultationRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.domain.entity.Consultation;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteConsultationUseCaseTest {

    @Mock
    private ConsultationRepositoryPort consultationRepositoryPort;
    @Mock
    private PetRepositoryPort petRepositoryPort;
    @InjectMocks
    private DeleteConsultationUseCase deleteConsultationUseCase;

    private UUID userId, petId, consultationId;
    private User user;
    private Pet pet;
    private Consultation consultation;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID(); petId = UUID.randomUUID(); consultationId = UUID.randomUUID();
        user = new User(); user.setId(userId);
        pet = new Pet(); pet.setId(petId); pet.setUser(user);
        consultation = new Consultation(); consultation.setId(consultationId); consultation.setPet(pet);
    }

    @Test
    void shouldDeleteConsultationSuccessfully() {
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(consultationRepositoryPort.findById(consultationId)).thenReturn(Optional.of(consultation));
        deleteConsultationUseCase.execute(petId, consultationId, userId);
        verify(consultationRepositoryPort).delete(consultation);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnPet() {
        User anotherUser = new User(); anotherUser.setId(UUID.randomUUID());
        pet.setUser(anotherUser);
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        assertThatThrownBy(() -> deleteConsultationUseCase.execute(petId, consultationId, userId))
                .isInstanceOf(BusinessException.class).hasMessageContaining("autenticado");
    }
}

