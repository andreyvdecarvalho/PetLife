package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetVetProfileUseCaseTest {

    @Mock
    private VeterinarianRepositoryPort veterinarianRepository;

    @InjectMocks
    private GetVetProfileUseCase useCase;

    @Test
    void shouldGetProfileSuccessfully() {
        UUID id = UUID.randomUUID();
        Veterinarian vet = new Veterinarian();
        vet.setId(id);

        when(veterinarianRepository.findById(id)).thenReturn(Optional.of(vet));

        VeterinarianResponse response = useCase.execute(id);
        assertNotNull(response);
    }

    @Test
    void shouldThrowExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(veterinarianRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> useCase.execute(id));
    }
}
