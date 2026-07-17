package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Grooming;
import com.petlife.modules.pet.entity.Pet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteGroomingUseCaseTest {

    @Mock
    private GroomingRepositoryPort groomingRepositoryPort;
    @Mock
    private PetRepositoryPort petRepositoryPort;
    @InjectMocks
    private DeleteGroomingUseCase deleteGroomingUseCase;

    private UUID userId, petId, groomingId;
    private User user;
    private Pet pet;
    private Grooming grooming;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID(); petId = UUID.randomUUID(); groomingId = UUID.randomUUID();
        user = new User(); user.setId(userId);
        pet = new Pet(); pet.setId(petId); pet.setUser(user);
        grooming = new Grooming(); grooming.setId(groomingId); grooming.setPet(pet);
    }

    @Test
    void shouldDeleteGroomingSuccessfully() {
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(groomingRepositoryPort.findById(groomingId)).thenReturn(Optional.of(grooming));
        deleteGroomingUseCase.execute(petId, groomingId, userId);
        verify(groomingRepositoryPort).delete(grooming);
    }
}
