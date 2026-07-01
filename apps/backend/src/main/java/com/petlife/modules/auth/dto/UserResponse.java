package com.petlife.modules.auth.dto;

import com.petlife.modules.auth.entity.User;
import com.petlife.modules.auth.entity.UserPlan;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String name,
    String email,
    String avatarUrl,
    String timezone,
    UserPlan plan,
    boolean emailVerified,
    LocalDateTime createdAt
) {
    /**
     * Factory method: cria UserResponse a partir da entidade User.
     * Elimina a necessidade de método toResponse() no AuthService.
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getTimezone(),
                user.getPlan(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }
}

