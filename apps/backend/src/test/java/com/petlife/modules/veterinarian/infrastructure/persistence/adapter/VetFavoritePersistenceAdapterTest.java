package com.petlife.modules.veterinarian.infrastructure.persistence.adapter;

import com.petlife.modules.veterinarian.entity.VetFavorite;
import com.petlife.modules.veterinarian.infrastructure.persistence.VetFavoriteJpaRepository;
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
class VetFavoritePersistenceAdapterTest {

    @Mock
    private VetFavoriteJpaRepository repository;

    @InjectMocks
    private VetFavoritePersistenceAdapter adapter;

    @Test
    void save() {
        VetFavorite entity = new VetFavorite();
        when(repository.save(entity)).thenReturn(entity);
        VetFavorite saved = adapter.save(entity);
        assertEquals(entity, saved);
        verify(repository, times(1)).save(entity);
    }

    @Test
    void findByUserId() {
        UUID userId = UUID.randomUUID();
        VetFavorite entity = new VetFavorite();
        when(repository.findByUserId(userId)).thenReturn(List.of(entity));
        
        List<VetFavorite> result = adapter.findByUserId(userId);
        assertEquals(1, result.size());
        assertEquals(entity, result.get(0));
    }

    @Test
    void findByUserIdAndVeterinarianId() {
        UUID userId = UUID.randomUUID();
        UUID vetId = UUID.randomUUID();
        VetFavorite entity = new VetFavorite();
        when(repository.findByUserIdAndVeterinarianId(userId, vetId)).thenReturn(Optional.of(entity));
        
        Optional<VetFavorite> result = adapter.findByUserIdAndVeterinarianId(userId, vetId);
        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    void existsByUserIdAndVeterinarianId() {
        UUID userId = UUID.randomUUID();
        UUID vetId = UUID.randomUUID();
        when(repository.existsByUserIdAndVeterinarianId(userId, vetId)).thenReturn(true);
        
        boolean result = adapter.existsByUserIdAndVeterinarianId(userId, vetId);
        assertTrue(result);
    }

    @Test
    void delete() {
        VetFavorite entity = new VetFavorite();
        adapter.delete(entity);
        verify(repository, times(1)).delete(entity);
    }
}
