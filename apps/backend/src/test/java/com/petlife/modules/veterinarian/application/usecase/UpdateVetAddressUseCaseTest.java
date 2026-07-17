package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.veterinarian.application.port.GeocodingPort;
import com.petlife.modules.veterinarian.application.port.VetAddressRepositoryPort;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.VetAddress;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.UpdateVetAddressRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateVetAddressUseCaseTest {

    @Mock
    private VeterinarianRepositoryPort veterinarianRepository;
    @Mock
    private VetAddressRepositoryPort vetAddressRepository;
    @Mock
    private GeocodingPort geocodingPort;

    @InjectMocks
    private UpdateVetAddressUseCase updateVetAddressUseCase;

    private UUID userId;
    private UUID addressId;
    private User user;
    private Veterinarian veterinarian;
    private VetAddress address;
    private UpdateVetAddressRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        
        veterinarian = new Veterinarian();
        veterinarian.setId(UUID.randomUUID());
        veterinarian.setUser(user);
        
        address = new VetAddress();
        address.setId(addressId);
        address.setVeterinarian(veterinarian);

        request = UpdateVetAddressRequest.builder()
                .label("Consultório")
                .street("Rua Nova")
                .number("100")
                .neighborhood("Centro")
                .city("São Paulo")
                .state("SP")
                .postalCode("01000-000")
                .isPrimary(true)
                .build();
    }

    @Test
    void shouldUpdateAddressSuccessfully() {
        when(veterinarianRepository.findByUserId(userId)).thenReturn(Optional.of(veterinarian));
        when(vetAddressRepository.findByIdAndVeterinarianId(addressId, veterinarian.getId())).thenReturn(Optional.of(address));
        when(geocodingPort.geocode(anyString(), anyString(), anyString()))
                .thenReturn(new GeocodingPort.GeocodingResult(new BigDecimal("-23.55"), new BigDecimal("-46.63")));
        when(vetAddressRepository.save(any(VetAddress.class))).thenAnswer(i -> i.getArguments()[0]);

        VetAddressResponse response = updateVetAddressUseCase.execute(userId, addressId, request);

        assertThat(response.street()).isEqualTo("Rua Nova");
        assertThat(response.latitude()).isEqualTo(new BigDecimal("-23.55"));
        verify(vetAddressRepository).save(address);
    }

    @Test
    void shouldThrowExceptionWhenVetNotFound() {
        when(veterinarianRepository.findByUserId(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> updateVetAddressUseCase.execute(userId, addressId, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void shouldThrowExceptionWhenAddressNotFound() {
        when(veterinarianRepository.findByUserId(userId)).thenReturn(Optional.of(veterinarian));
        when(vetAddressRepository.findByIdAndVeterinarianId(addressId, veterinarian.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> updateVetAddressUseCase.execute(userId, addressId, request))
                .isInstanceOf(BusinessException.class);
    }
}
