package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.RoutineActivityRepositoryPort;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.entity.RoutineActivity;
import com.petlife.modules.pet.entity.RoutineActivityStatus;
import com.petlife.modules.pet.entity.RoutineActivityType;
import com.petlife.modules.pet.infrastructure.dto.CreateRoutineActivityRequest;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRoutineActivityUseCaseTest {

    @Mock
    private RoutineActivityRepositoryPort routineActivityRepository;

    @Mock
    private PetRepositoryPort petRepository;

    @InjectMocks
    private CreateRoutineActivityUseCase useCase;

    private UUID petId;
    private CreateRoutineActivityRequest request;
    private Pet pet;

    @BeforeEach
    void setUp() {
        petId = UUID.randomUUID();
        request = new CreateRoutineActivityRequest(
                "Morning Walk",
                "Park walk",
                LocalDate.now(),
                LocalTime.of(8, 0),
                RoutineActivityType.WALK,
                RoutineActivityStatus.SCHEDULED
        );
        pet = new Pet();
        pet.setId(petId);
    }

    @Test
    void shouldCreateRoutineActivitySuccessfully() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(routineActivityRepository.save(any(RoutineActivity.class))).thenAnswer(i -> {
            RoutineActivity activity = i.getArgument(0);
            activity.setId(UUID.randomUUID());
            return activity;
        });

        RoutineActivity result = useCase.execute(petId, request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Morning Walk");
        assertThat(result.getPet()).isEqualTo(pet);
        verify(routineActivityRepository).save(any(RoutineActivity.class));
    }

    @Test
    void shouldThrowExceptionWhenPetNotFound() {
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(petId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Pet not found");
    }
}
