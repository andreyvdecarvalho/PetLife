package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.Veterinarian;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMyVetProfileUseCaseTest {

    @Mock
    private VeterinarianRepositoryPort veterinarianRepository;

    @InjectMocks
    private GetMyVetProfileUseCase getMyVetProfileUseCase;

    private UUID userId;
    private Veterinarian veterinarian;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        veterinarian = new Veterinarian();
        veterinarian.setId(UUID.randomUUID());
        veterinarian.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));
        veterinarian.setCrmvNumber("12345");
        veterinarian.setFullName("Dr. Teste");
    }

    @Test
    void shouldReturnProfileSuccessfully() {
        when(veterinarianRepository.findByUserId(userId)).thenReturn(Optional.of(veterinarian));
        VeterinarianResponse response = getMyVetProfileUseCase.execute(userId);
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(veterinarian.getId());
        assertThat(response.getCrmvNumber()).isEqualTo("12345");
        verify(veterinarianRepository).findByUserId(userId);
    }

    @Test
    void shouldThrowExceptionWhenProfileNotFound() {
        when(veterinarianRepository.findByUserId(userId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> getMyVetProfileUseCase.execute(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Perfil de veterinário não encontrado");
    }
}


