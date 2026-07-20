package com.petlife.modules.auth.application.dto;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.domain.entity.UserPlan;
import com.petlife.modules.auth.domain.entity.Timezone;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String name,
    String nickname,
    String email,
    String phone,
    String avatarUrl,
    Timezone timezone,
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
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getTimezone(),
                user.getPlan(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }
}

