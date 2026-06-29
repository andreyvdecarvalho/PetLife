package com.petlife.modules.auth.dto;

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
) {}
