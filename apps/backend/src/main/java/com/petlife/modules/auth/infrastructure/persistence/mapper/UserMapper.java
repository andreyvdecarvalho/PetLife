package com.petlife.modules.auth.infrastructure.persistence.mapper;

import com.petlife.modules.auth.domain.entity.User;
import com.petlife.modules.auth.infrastructure.persistence.entity.UserJpaEntity;

public class UserMapper {

    public static User toDomain(UserJpaEntity jpa) {
        if (jpa == null) return null;
        
        User user = new User();
        user.setId(jpa.getId());
        user.setCreatedAt(jpa.getCreatedAt());
        user.setUpdatedAt(jpa.getUpdatedAt());
        user.setName(jpa.getName());
        user.setEmail(jpa.getEmail());
        user.setPasswordHash(jpa.getPasswordHash());
        user.setAvatarUrl(jpa.getAvatarUrl());
        user.setNickname(jpa.getNickname());
        user.setPhone(jpa.getPhone());
        user.setTimezone(jpa.getTimezone());
        user.setPlan(jpa.getPlan());
        user.setEmailVerified(jpa.isEmailVerified());
        user.setLgpdAcceptedAt(jpa.getLgpdAcceptedAt());
        user.setDeletedAt(jpa.getDeletedAt());
        user.setFcmToken(jpa.getFcmToken());
        
        return user;
    }

    public static UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) return null;
        
        UserJpaEntity jpa = new UserJpaEntity();
        jpa.setId(domain.getId());
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt());
        jpa.setName(domain.getName());
        jpa.setEmail(domain.getEmail());
        jpa.setPasswordHash(domain.getPasswordHash());
        jpa.setAvatarUrl(domain.getAvatarUrl());
        jpa.setNickname(domain.getNickname());
        jpa.setPhone(domain.getPhone());
        jpa.setTimezone(domain.getTimezone());
        jpa.setPlan(domain.getPlan());
        jpa.setEmailVerified(domain.isEmailVerified());
        jpa.setLgpdAcceptedAt(domain.getLgpdAcceptedAt());
        jpa.setDeletedAt(domain.getDeletedAt());
        jpa.setFcmToken(domain.getFcmToken());
        
        return jpa;
    }
}
