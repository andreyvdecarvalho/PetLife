package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.application.port.RoutineActivityRepositoryPort;
import com.petlife.modules.pet.domain.entity.RoutineActivity;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.petlife.modules.pet.domain.entity.Pet;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListRoutineActivitiesByPetUseCaseTest {

    @Mock
    private RoutineActivityRepositoryPort routineActivityRepository;

    @Mock
    private PetRepositoryPort petRepository;

    @InjectMocks
    private ListRoutineActivitiesByPetUseCase useCase;

    @Test
    void shouldListActivitiesSuccessfullyWithoutDate() {
        UUID petId = UUID.randomUUID();
        when(petRepository.findById(petId)).thenReturn(Optional.of(new Pet()));
        
        RoutineActivity activity = new RoutineActivity();
        when(routineActivityRepository.findByPetIdOrderByActivityDateAscActivityTimeAsc(petId))
                .thenReturn(List.of(activity));

        List<RoutineActivity> result = useCase.execute(petId, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldListActivitiesSuccessfullyWithDate() {
        UUID petId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        when(petRepository.findById(petId)).thenReturn(Optional.of(new Pet()));
        
        RoutineActivity activity = new RoutineActivity();
        when(routineActivityRepository.findByPetIdAndActivityDateOrderByActivityTimeAsc(petId, date))
                .thenReturn(List.of(activity));

        List<RoutineActivity> result = useCase.execute(petId, date);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldThrowExceptionWhenPetNotFound() {
        UUID petId = UUID.randomUUID();
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(petId, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Pet not found");
    }
}
