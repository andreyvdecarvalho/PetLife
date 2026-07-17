package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.veterinarian.application.port.VetFavoriteRepositoryPort;
import com.petlife.modules.veterinarian.entity.VetFavorite;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListFavoriteVetsUseCaseTest {

    @Mock
    private VetFavoriteRepositoryPort vetFavoriteRepository;

    @InjectMocks
    private ListFavoriteVetsUseCase listFavoriteVetsUseCase;

    private UUID userId;
    private User user;
    private VetFavorite favorite;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        
        Veterinarian vet = new Veterinarian();
        vet.setId(UUID.randomUUID());
        vet.setCrmvNumber("12345");
        vet.setFullName("Vet Favorite");

        favorite = new VetFavorite();
        favorite.setId(UUID.randomUUID());
        favorite.setUser(user);
        favorite.setVeterinarian(vet);
    }

    @Test
    void shouldReturnFavoritesList() {
        when(vetFavoriteRepository.findByUserId(userId)).thenReturn(List.of(favorite));
        List<VeterinarianResponse> responses = listFavoriteVetsUseCase.execute(userId);
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(favorite.getVeterinarian().getId());
        assertThat(responses.get(0).getFullName()).isEqualTo("Vet Favorite");
    }

    @Test
    void shouldReturnEmptyListWhenNoFavorites() {
        when(vetFavoriteRepository.findByUserId(userId)).thenReturn(List.of());
        List<VeterinarianResponse> responses = listFavoriteVetsUseCase.execute(userId);
        assertThat(responses).isEmpty();
    }
}
