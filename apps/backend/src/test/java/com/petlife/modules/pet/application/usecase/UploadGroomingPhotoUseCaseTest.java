package com.petlife.modules.pet.application.usecase;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.pet.application.port.GroomingRepositoryPort;
import com.petlife.modules.pet.application.port.PetRepositoryPort;
import com.petlife.modules.pet.entity.Grooming;
import com.petlife.modules.pet.entity.GroomingType;
import com.petlife.modules.pet.entity.Pet;
import com.petlife.modules.pet.infrastructure.dto.GroomingResponse;
import com.petlife.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadGroomingPhotoUseCaseTest {

    @Mock
    private GroomingRepositoryPort groomingRepositoryPort;

    @Mock
    private PetRepositoryPort petRepositoryPort;

    @InjectMocks
    private UploadGroomingPhotoUseCase useCase;

    private UUID userId;
    private UUID petId;
    private Pet pet;
    private UUID groomingId;
    private Grooming grooming;
    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        petId = UUID.randomUUID();
        groomingId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        pet = new Pet();
        pet.setId(petId);
        pet.setUser(com.petlife.modules.auth.infrastructure.persistence.mapper.UserMapper.toJpaEntity(user));

        grooming = new Grooming();
        grooming.setId(groomingId);
        grooming.setPet(pet);
        grooming.setType(GroomingType.BATH);
        grooming.setDate(LocalDate.now());
        grooming.setPhotos(new ArrayList<>());

        file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
    }

    @Test
    void shouldUploadBeforePhotoSuccessfully() {
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(groomingRepositoryPort.findById(groomingId)).thenReturn(Optional.of(grooming));
        when(groomingRepositoryPort.save(any(Grooming.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GroomingResponse response = useCase.execute(petId, groomingId, userId, file, "before");

        assertNotNull(response);
        assertEquals(1, response.getPhotos().size());
        assertTrue(response.getPhotos().get(0).startsWith("data:image/jpeg;base64,"));
    }

    @Test
    void shouldUploadAfterPhotoSuccessfully() {
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(groomingRepositoryPort.findById(groomingId)).thenReturn(Optional.of(grooming));
        when(groomingRepositoryPort.save(any(Grooming.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GroomingResponse response = useCase.execute(petId, groomingId, userId, file, "after");

        assertNotNull(response);
        assertEquals(2, response.getPhotos().size());
        assertTrue(response.getPhotos().get(1).startsWith("data:image/jpeg;base64,"));
    }

    @Test
    void shouldReplaceExistingPhotoOfSameType() {
        grooming.getPhotos().add("/uploads/grooming-" + groomingId + "-before.jpg");

        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(groomingRepositoryPort.findById(groomingId)).thenReturn(Optional.of(grooming));
        when(groomingRepositoryPort.save(any(Grooming.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GroomingResponse response = useCase.execute(petId, groomingId, userId, file, "before");

        assertNotNull(response);
        assertEquals(1, response.getPhotos().size());
        assertTrue(response.getPhotos().get(0).startsWith("data:image/jpeg;base64,"));
    }

    @Test
    void shouldThrowExceptionWhenPhotoTypeInvalid() {
        when(petRepositoryPort.findById(petId)).thenReturn(Optional.of(pet));
        when(groomingRepositoryPort.findById(groomingId)).thenReturn(Optional.of(grooming));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, groomingId, userId, file, "invalid"));

        assertEquals("INVALID_PHOTO_TYPE", exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> useCase.execute(petId, groomingId, userId, emptyFile, "before"));

        assertEquals("FILE_EMPTY", exception.getCode());
    }

}
