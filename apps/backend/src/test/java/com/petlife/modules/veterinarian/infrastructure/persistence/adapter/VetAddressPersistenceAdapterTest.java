package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.entity.VetAddress;
import com.petlife.modules.veterinarian.infrastructure.persistence.VetAddressJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VetAddressPersistenceAdapterTest {

    @Mock
    private VetAddressJpaRepository repository;

    @InjectMocks
    private VetAddressPersistenceAdapter adapter;

    @Test
    void save() {
        VetAddress entity = new VetAddress();
        when(repository.save(entity)).thenReturn(entity);
        VetAddress saved = adapter.save(entity);
        assertEquals(entity, saved);
        verify(repository, times(1)).save(entity);
    }

    @Test
    void findByIdAndVeterinarianId() {
        UUID id = UUID.randomUUID();
        UUID vetId = UUID.randomUUID();
        VetAddress entity = new VetAddress();
        when(repository.findByIdAndVeterinarianId(id, vetId)).thenReturn(Optional.of(entity));
        
        Optional<VetAddress> result = adapter.findByIdAndVeterinarianId(id, vetId);
        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    void findByVeterinarianId() {
        UUID vetId = UUID.randomUUID();
        VetAddress entity = new VetAddress();
        when(repository.findByVeterinarianId(vetId)).thenReturn(List.of(entity));
        
        List<VetAddress> result = adapter.findByVeterinarianId(vetId);
        assertEquals(1, result.size());
        assertEquals(entity, result.get(0));
    }

    @Test
    void delete() {
        VetAddress entity = new VetAddress();
        adapter.delete(entity);
        verify(repository, times(1)).delete(entity);
    }
}
