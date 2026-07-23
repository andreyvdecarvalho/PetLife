package com.petlife.modules.veterinarian.application.usecase;

import com.petlife.modules.auth.application.port.UserRepositoryPort;
import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.veterinarian.application.port.VeterinarianRepositoryPort;
import com.petlife.modules.veterinarian.domain.exception.CrmvAlreadyExistsException;
import com.petlife.modules.veterinarian.domain.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.CreateVeterinarianRequest;
import com.petlife.modules.veterinarian.infrastructure.dto.response.VeterinarianResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateVeterinarianProfileUseCaseTest {

    @Mock
    private VeterinarianRepositoryPort veterinarianRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private CreateVeterinarianProfileUseCase useCase;

    private User mockUser;
    private CreateVeterinarianRequest validRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());

        validRequest = new CreateVeterinarianRequest();
        validRequest.setCrmvNumber("12345");
        validRequest.setCrmvState("SP");
        validRequest.setFullName("Dr. Vet");
        validRequest.setBio("Bio here");
        validRequest.setSpecialties(List.of("Cirurgia"));
    }

    @Test
    void shouldCreateVeterinarianSuccessfully() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(veterinarianRepository.existsByCrmvNumber(validRequest.getCrmvNumber())).thenReturn(false);
        
        Veterinarian savedVet = new Veterinarian();
        savedVet.setId(UUID.randomUUID());
        savedVet.setCrmvNumber(validRequest.getCrmvNumber());
        savedVet.setCrmvState(validRequest.getCrmvState());
        
        when(veterinarianRepository.save(any(Veterinarian.class))).thenReturn(savedVet);

        VeterinarianResponse response = useCase.execute(mockUser.getId(), validRequest);

        assertNotNull(response);
        verify(veterinarianRepository, times(1)).save(any(Veterinarian.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> useCase.execute(mockUser.getId(), validRequest));
        verify(veterinarianRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCrmvAlreadyExists() {
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(veterinarianRepository.existsByCrmvNumber(validRequest.getCrmvNumber())).thenReturn(true);

        assertThrows(CrmvAlreadyExistsException.class, () -> useCase.execute(mockUser.getId(), validRequest));
        verify(veterinarianRepository, never()).save(any());
    }
}
