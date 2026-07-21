package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.VaccinationPort;
import com.petlife.modules.pet.domain.entity.Pet;
import com.petlife.modules.pet.domain.entity.Vaccination;
import com.petlife.modules.pet.infrastructure.dto.VaccinationResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadVaccinationProofUseCaseTest {

    @Mock
    private VaccinationPort vaccinationPort;

    @InjectMocks
    private UploadVaccinationProofUseCase useCase;

    private UUID userId;
    private UUID petId;
    private UUID vaccineId;
    private Pet pet;
    private User user;
    private Vaccination vaccination;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();
        vaccineId = UUID.randomUUID();
        
        user = new User();
        user.setId(userId);
        
        pet = new Pet();
        pet.setId(petId);
        pet.setUser(user);
        
        vaccination = new Vaccination();
        vaccination.setId(vaccineId);
        vaccination.setPet(pet);
        vaccination.setVaccineName("V10");
        vaccination.setDateAdministered(LocalDate.now());
    }

    @Test
    void shouldUploadProofSuccessfully() {
        MultipartFile file = new MockMultipartFile("file", "proof.jpg", "image/jpeg", "image content".getBytes());

        when(vaccinationPort.findById(vaccineId)).thenReturn(Optional.of(vaccination));
        when(vaccinationPort.save(any(Vaccination.class))).thenReturn(vaccination);

        VaccinationResponse response = useCase.execute(petId, vaccineId, userId, file);

        assertNotNull(response);
        assertTrue(response.getProofUrl().contains(vaccineId.toString()));
        verify(vaccinationPort).save(any(Vaccination.class));
    }

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        BusinessException exception = assertThrows(BusinessException.class, 
            () -> useCase.execute(petId, vaccineId, userId, emptyFile));
            
        assertEquals("FILE_EMPTY", exception.getCode());
    }
}

