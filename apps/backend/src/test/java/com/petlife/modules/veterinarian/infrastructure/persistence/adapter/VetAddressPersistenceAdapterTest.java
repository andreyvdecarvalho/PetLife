package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.domain.entity.VetAddress;
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
        when(repository.save(any())).thenReturn(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toJpaEntity(entity));
        VetAddress saved = adapter.save(entity);
        assertEquals(entity.getId(), saved.getId());
        verify(repository, times(1)).save(any());
    }

    @Test
    void findByIdAndVeterinarianId() {
        UUID id = UUID.randomUUID();
        UUID vetId = UUID.randomUUID();
        VetAddress entity = new VetAddress();
        when(repository.findByIdAndVeterinarianId(id, vetId)).thenReturn(Optional.of(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toJpaEntity(entity)));
        
        Optional<VetAddress> result = adapter.findByIdAndVeterinarianId(id, vetId);
        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
    }

    @Test
    void findByVeterinarianId() {
        UUID vetId = UUID.randomUUID();
        VetAddress entity = new VetAddress();
        when(repository.findByVeterinarianId(vetId)).thenReturn(List.of(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toJpaEntity(entity)));
        
        List<VetAddress> result = adapter.findByVeterinarianId(vetId);
        assertEquals(1, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
    }

    @Test
    void delete() {
        VetAddress entity = new VetAddress();
        adapter.delete(entity);
        verify(repository, times(1)).delete(any());
    }
}
