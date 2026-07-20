package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.veterinarian.application.port.VetAddressRepositoryPort;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.VetAddress;
import com.petlife.modules.veterinarian.entity.Veterinarian;
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
class DeleteVetAddressUseCaseTest {

    @Mock
    private VeterinarianRepositoryPort veterinarianRepository;
    @Mock
    private VetAddressRepositoryPort vetAddressRepository;

    @InjectMocks
    private DeleteVetAddressUseCase deleteVetAddressUseCase;

    private UUID userId;
    private UUID addressId;
    private User user;
    private Veterinarian veterinarian;
    private VetAddress address;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();
        
        user = new User();
        user.setId(userId);
        
        veterinarian = new Veterinarian();
        veterinarian.setId(UUID.randomUUID());
        veterinarian.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));
        
        address = new VetAddress();
        address.setId(addressId);
        address.setVeterinarian(veterinarian);
    }

    @Test
    void shouldDeleteAddressSuccessfully() {
        when(veterinarianRepository.findByUserId(userId)).thenReturn(Optional.of(veterinarian));
        when(vetAddressRepository.findByIdAndVeterinarianId(addressId, veterinarian.getId())).thenReturn(Optional.of(address));

        deleteVetAddressUseCase.execute(userId, addressId);

        verify(vetAddressRepository).delete(address);
    }

    @Test
    void shouldThrowExceptionWhenAddressNotFound() {
        when(veterinarianRepository.findByUserId(userId)).thenReturn(Optional.of(veterinarian));
        when(vetAddressRepository.findByIdAndVeterinarianId(addressId, veterinarian.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deleteVetAddressUseCase.execute(userId, addressId))
                .isInstanceOf(BusinessException.class);
    }
}
