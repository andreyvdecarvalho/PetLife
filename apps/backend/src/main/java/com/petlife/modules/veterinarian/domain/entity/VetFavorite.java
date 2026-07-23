package com.petlife.modules.veterinarian.domain.entity;

import com.petlife.modules.auth.domain.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
public class VetFavorite {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Veterinarian veterinarian;
    private User user;
}
