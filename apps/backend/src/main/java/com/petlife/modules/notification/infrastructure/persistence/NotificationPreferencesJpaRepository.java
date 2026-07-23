package com.petlife.modules.notification.infrastructure.persistence;

import com.petlife.modules.notification.infrastructure.persistence.entity.NotificationPreferencesJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface NotificationPreferencesJpaRepository extends JpaRepository<NotificationPreferencesJpaEntity, UUID> {
}
