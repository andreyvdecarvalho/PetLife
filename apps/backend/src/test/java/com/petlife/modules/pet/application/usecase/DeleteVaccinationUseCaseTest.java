package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.Vaccination;
import com.petlife.modules.auth.domain.entity.User;
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
class DeleteVaccinationUseCaseTest {

    @Mock
    private VaccinationPort vaccinationPort;

    @Mock
    private PetRepositoryPort petRepositoryPort;

    @InjectMocks
    private DeleteVaccinationUseCase deleteVaccinationUseCase;

    private UUID userId;
    private UUID petId;
    private UUID vaccinationId;
    private User user;
    private Pet pet;
    private Vaccination vaccination;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();
        vaccinationId = UUID.randomUUID();
        user = new User(); user.setId(userId);
        pet = new Pet(); pet.setId(petId); pet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));
        vaccination = new Vaccination(); vaccination.setId(vaccinationId); vaccination.setPet(pet);
    }

    @Test
    void shouldDeleteVaccinationSuccessfully() {
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(vaccinationPort.findById(vaccinationId)).thenReturn(Optional.of(vaccination));
        deleteVaccinationUseCase.execute(petId, vaccinationId, userId);
        verify(vaccinationPort).delete(vaccination);
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotOwnPet() {
        User anotherUser = new User(); anotherUser.setId(UUID.randomUUID());
        pet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(anotherUser));
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        assertThatThrownBy(() -> deleteVaccinationUseCase.execute(petId, vaccinationId, userId))
                .isInstanceOf(BusinessException.class).hasMessageContaining("autenticado");
    }
}
