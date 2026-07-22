package com.petlife.modules.veterinarian.infrastructure.persistence.entity;

import com.petlife.modules.auth.infrastructure.persistence.entity.UserJpaEntity;
import com.petlife.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vet_favorites")
@Getter
@Setter
public class VetFavoriteJpaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", nullable = false)
    private VeterinarianJpaEntity veterinarian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;
}
