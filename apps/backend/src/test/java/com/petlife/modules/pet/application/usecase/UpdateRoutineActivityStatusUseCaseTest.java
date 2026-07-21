package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.pet.application.port.RoutineActivityRepositoryPort;
import com.petlife.modules.pet.domain.entity.RoutineActivity;
import com.petlife.modules.pet.domain.entity.RoutineActivityStatus;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRoutineActivityStatusUseCaseTest {

    @Mock
    private RoutineActivityRepositoryPort routineActivityRepository;

    @InjectMocks
    private UpdateRoutineActivityStatusUseCase useCase;

    @Test
    void shouldUpdateStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        RoutineActivity activity = new RoutineActivity();
        activity.setStatus(RoutineActivityStatus.PENDING);
        
        when(routineActivityRepository.findById(id)).thenReturn(Optional.of(activity));
        when(routineActivityRepository.save(any(RoutineActivity.class))).thenReturn(activity);

        RoutineActivity result = useCase.execute(id, RoutineActivityStatus.COMPLETED);

        assertThat(result.getStatus()).isEqualTo(RoutineActivityStatus.COMPLETED);
        verify(routineActivityRepository).save(activity);
    }

    @Test
    void shouldThrowExceptionWhenActivityNotFound() {
        UUID id = UUID.randomUUID();
        when(routineActivityRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(id, RoutineActivityStatus.COMPLETED))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Routine activity not found");
    }
}
