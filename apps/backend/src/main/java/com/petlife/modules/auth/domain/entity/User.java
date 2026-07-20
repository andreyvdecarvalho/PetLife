package com.petlife.modules.auth.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class User {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String name;
    private String email;
    private String passwordHash;
    private String avatarUrl;
    private String nickname;
    private String phone;
    private Timezone timezone = Timezone.AMERICA_SAO_PAULO;
    private UserPlan plan = UserPlan.FREE;
    private boolean emailVerified = false;
    private LocalDateTime lgpdAcceptedAt;
    private LocalDateTime deletedAt;
    private String fcmToken;
    
    public User() {
    }
}
