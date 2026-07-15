package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.GeocodingPort;
import com.petlife.modules.veterinarian.application.port.VetAddressRepositoryPort;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.VetAddress;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.AddVetAddressRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VetAddressResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddVetAddressUseCaseTest {

    @Mock
    private VeterinarianRepositoryPort veterinarianRepository;

    @Mock
    private VetAddressRepositoryPort vetAddressRepository;

    @Mock
    private GeocodingPort geocodingPort;

    @InjectMocks
    private AddVetAddressUseCase useCase;

    private Veterinarian mockVet;
    private AddVetAddressRequest validRequest;

    @BeforeEach
    void setUp() {
        mockVet = new Veterinarian();
        mockVet.setId(UUID.randomUUID());

        validRequest = new AddVetAddressRequest(
                "Clínica Principal", "Rua X", "123", "", "Bairro Y", "Cidade Z", "SP", "00000-000", true
        );
    }

    @Test
    void shouldAddAddressSuccessfully() {
        when(veterinarianRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(mockVet));
        when(geocodingPort.geocode(anyString(), anyString(), anyString()))
                .thenReturn(new GeocodingPort.GeocodingResult(BigDecimal.valueOf(-23.5), BigDecimal.valueOf(-46.6)));
        
        VetAddress savedAddress = new VetAddress();
        savedAddress.setId(UUID.randomUUID());
        savedAddress.setLatitude(BigDecimal.valueOf(-23.5));
        savedAddress.setLongitude(BigDecimal.valueOf(-46.6));

        when(vetAddressRepository.save(any(VetAddress.class))).thenReturn(savedAddress);

        VetAddressResponse response = useCase.execute(UUID.randomUUID(), validRequest);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(-23.5), response.latitude());
        verify(vetAddressRepository, times(1)).save(any(VetAddress.class));
    }

    @Test
    void shouldThrowExceptionWhenVetNotFound() {
        when(veterinarianRepository.findByUserId(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> useCase.execute(UUID.randomUUID(), validRequest));
        verify(vetAddressRepository, never()).save(any());
    }
}
