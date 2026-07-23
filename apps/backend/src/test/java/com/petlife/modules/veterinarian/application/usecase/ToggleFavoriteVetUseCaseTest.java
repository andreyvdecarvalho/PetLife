package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.veterinarian.application.port.VetFavoriteRepositoryPort;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.entity.VetFavorite;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleFavoriteVetUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private VeterinarianRepositoryPort veterinarianRepository;

    @Mock
    private VetFavoriteRepositoryPort vetFavoriteRepository;

    @InjectMocks
    private ToggleFavoriteVetUseCase useCase;

    private User mockUser;
    private Veterinarian mockVet;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());

        mockVet = new Veterinarian();
        mockVet.setId(UUID.randomUUID());
    }

    @Test
    void shouldAddFavoriteWhenNotExists() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(veterinarianRepository.findById(mockVet.getId())).thenReturn(Optional.of(mockVet));
        when(vetFavoriteRepository.findByUserIdAndVeterinarianId(mockUser.getId(), mockVet.getId()))
                .thenReturn(Optional.empty());

        useCase.execute(mockUser.getId(), mockVet.getId());

        verify(vetFavoriteRepository, times(1)).save(any(VetFavorite.class));
        verify(vetFavoriteRepository, never()).delete(any(VetFavorite.class));
    }

    @Test
    void shouldRemoveFavoriteWhenAlreadyExists() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(veterinarianRepository.findById(mockVet.getId())).thenReturn(Optional.of(mockVet));
        
        VetFavorite existingFavorite = new VetFavorite();
        when(vetFavoriteRepository.findByUserIdAndVeterinarianId(mockUser.getId(), mockVet.getId()))
                .thenReturn(Optional.of(existingFavorite));

        useCase.execute(mockUser.getId(), mockVet.getId());

        verify(vetFavoriteRepository, never()).save(any(VetFavorite.class));
        verify(vetFavoriteRepository, times(1)).delete(existingFavorite);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> useCase.execute(mockUser.getId(), mockVet.getId()));
        verify(vetFavoriteRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenVetNotFound() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(veterinarianRepository.findById(mockVet.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> useCase.execute(mockUser.getId(), mockVet.getId()));
        verify(vetFavoriteRepository, never()).save(any());
    }
}
