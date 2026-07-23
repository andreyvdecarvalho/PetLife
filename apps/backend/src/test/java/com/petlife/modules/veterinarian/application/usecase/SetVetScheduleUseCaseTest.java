package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VetScheduleRepositoryPort;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.VetSchedule;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.SetVetScheduleRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VetScheduleResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetVetScheduleUseCaseTest {

    @Mock
    private VeterinarianRepositoryPort veterinarianRepository;

    @Mock
    private VetScheduleRepositoryPort vetScheduleRepository;

    @InjectMocks
    private SetVetScheduleUseCase useCase;

    private Veterinarian mockVet;
    private SetVetScheduleRequest validRequest;

    @BeforeEach
    void setUp() {
        mockVet = new Veterinarian();
        mockVet.setId(UUID.randomUUID());

        validRequest = new SetVetScheduleRequest(
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0), true
        );
    }

    @Test
    void shouldSetScheduleSuccessfully() {
        when(veterinarianRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(mockVet));
        
        VetSchedule savedSchedule = new VetSchedule();
        savedSchedule.setId(UUID.randomUUID());
        savedSchedule.setDayOfWeek(validRequest.dayOfWeek());
        
        when(vetScheduleRepository.save(any(VetSchedule.class))).thenReturn(savedSchedule);

        VetScheduleResponse response = useCase.execute(UUID.randomUUID(), validRequest);

        assertNotNull(response);
        assertEquals(DayOfWeek.MONDAY, response.dayOfWeek());
        verify(vetScheduleRepository, times(1)).save(any(VetSchedule.class));
    }

    @Test
    void shouldThrowExceptionWhenCloseTimeBeforeOpenTime() {
        when(veterinarianRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(mockVet));
        
        SetVetScheduleRequest invalidRequest = new SetVetScheduleRequest(
                DayOfWeek.MONDAY, LocalTime.of(18, 0), LocalTime.of(8, 0), true
        );

        assertThrows(BusinessException.class, () -> useCase.execute(UUID.randomUUID(), invalidRequest));
        verify(vetScheduleRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenVetNotFound() {
        when(veterinarianRepository.findByUserId(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> useCase.execute(UUID.randomUUID(), validRequest));
        verify(vetScheduleRepository, never()).save(any());
    }
}
