package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.domain.entity.VetSchedule;
import com.petlife.modules.veterinarian.infrastructure.persistence.VetScheduleJpaRepository;
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
class VetSchedulePersistenceAdapterTest {

    @Mock
    private VetScheduleJpaRepository repository;

    @InjectMocks
    private VetSchedulePersistenceAdapter adapter;

    @Test
    void save() {
        VetSchedule entity = new VetSchedule();
        when(repository.save(any())).thenReturn(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toJpaEntity(entity));
        VetSchedule saved = adapter.save(entity);
        assertEquals(entity.getId(), saved.getId());
        verify(repository, times(1)).save(any());
    }

    @Test
    void findByIdAndVeterinarianId() {
        UUID id = UUID.randomUUID();
        UUID vetId = UUID.randomUUID();
        VetSchedule entity = new VetSchedule();
        when(repository.findByIdAndVeterinarianId(id, vetId)).thenReturn(Optional.of(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toJpaEntity(entity)));
        
        Optional<VetSchedule> result = adapter.findByIdAndVeterinarianId(id, vetId);
        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
    }

    @Test
    void findByVeterinarianId() {
        UUID vetId = UUID.randomUUID();
        VetSchedule entity = new VetSchedule();
        when(repository.findByVeterinarianId(vetId)).thenReturn(List.of(com.petlife.modules.veterinarian.infrastructure.persistence.mapper.VeterinarianMapper.toJpaEntity(entity)));
        
        List<VetSchedule> result = adapter.findByVeterinarianId(vetId);
        assertEquals(1, result.size());
        assertEquals(entity.getId(), result.get(0).getId());
    }

    @Test
    void delete() {
        VetSchedule entity = new VetSchedule();
        adapter.delete(entity);
        verify(repository, times(1)).delete(any());
    }
}
