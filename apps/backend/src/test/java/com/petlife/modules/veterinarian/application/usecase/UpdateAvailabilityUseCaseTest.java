package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.AvailabilityStatus;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.UpdateAvailabilityRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
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
class UpdateAvailabilityUseCaseTest {

    @Mock
    private VeterinarianRepositoryPort veterinarianRepository;

    @InjectMocks
    private UpdateAvailabilityUseCase useCase;

    private Veterinarian mockVet;
    private UpdateAvailabilityRequest validRequest;

    @BeforeEach
    void setUp() {
        mockVet = new Veterinarian();
        mockVet.setId(UUID.randomUUID());
        mockVet.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE);
        mockVet.setEmergencyOnDuty(false);

        validRequest = new UpdateAvailabilityRequest(AvailabilityStatus.AVAILABLE, true);
    }

    @Test
    void shouldUpdateAvailabilitySuccessfully() {
        when(veterinarianRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(mockVet));
        when(veterinarianRepository.save(any(Veterinarian.class))).thenReturn(mockVet);

        VeterinarianResponse response = useCase.execute(UUID.randomUUID(), validRequest);

        assertNotNull(response);
        assertEquals(AvailabilityStatus.AVAILABLE, mockVet.getAvailabilityStatus());
        assertTrue(mockVet.isEmergencyOnDuty());
        verify(veterinarianRepository, times(1)).save(mockVet);
    }

    @Test
    void shouldThrowExceptionWhenVetNotFound() {
        when(veterinarianRepository.findByUserId(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> useCase.execute(UUID.randomUUID(), validRequest));
        verify(veterinarianRepository, never()).save(any());
    }
}
