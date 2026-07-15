package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.entity.Modality;
import com.petlife.modules.veterinarian.entity.Veterinarian;
import com.petlife.modules.veterinarian.infrastructure.dto.request.SearchVeterinariansRequest;
import com.petlife.modules.veterinarian.infrastructure.persistence.VeterinarianJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeterinarianPersistenceAdapterTest {

    @Mock
    private VeterinarianJpaRepository repository;

    @InjectMocks
    private VeterinarianPersistenceAdapter adapter;

    @Test
    void save() {
        Veterinarian entity = new Veterinarian();
        when(repository.save(entity)).thenReturn(entity);
        Veterinarian saved = adapter.save(entity);
        assertEquals(entity, saved);
        verify(repository, times(1)).save(entity);
    }

    @Test
    void findById() {
        UUID id = UUID.randomUUID();
        Veterinarian entity = new Veterinarian();
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        
        Optional<Veterinarian> result = adapter.findById(id);
        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    void findByUserId() {
        UUID userId = UUID.randomUUID();
        Veterinarian entity = new Veterinarian();
        when(repository.findByUserId(userId)).thenReturn(Optional.of(entity));
        
        Optional<Veterinarian> result = adapter.findByUserId(userId);
        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    void existsByCrmvNumber() {
        when(repository.existsByCrmvNumber("12345")).thenReturn(true);
        
        boolean result = adapter.existsByCrmvNumber("12345");
        assertTrue(result);
    }

    @Test
    void search() {
        SearchVeterinariansRequest request = SearchVeterinariansRequest.builder()
                .latitude(BigDecimal.ZERO)
                .longitude(BigDecimal.ZERO)
                .radiusKm(10.0)
                .modality(Modality.CLINIC)
                .specialty("Geral")
                .emergencyOnDuty(true)
                .page(0)
                .size(10)
                .build();

        Veterinarian vet = new Veterinarian();
        Page<Veterinarian> page = new PageImpl<>(List.of(vet));

        when(repository.searchVeterinarians(
                eq(BigDecimal.ZERO),
                eq(BigDecimal.ZERO),
                eq(10.0),
                eq("CLINIC"),
                eq("Geral"),
                eq(true),
                any(PageRequest.class)
        )).thenReturn(page);

        Page<Veterinarian> result = adapter.search(request);

        assertEquals(1, result.getContent().size());
        assertEquals(vet, result.getContent().get(0));
    }
}
