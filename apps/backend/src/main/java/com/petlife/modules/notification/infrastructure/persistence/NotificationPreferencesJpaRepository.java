package com.petlife.modules.notification.infrastructure.persistence;

import com.petlife.modules.notification.domain.entity.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface NotificationPreferencesJpaRepository extends JpaRepository<NotificationPreferences, UUID> {
}
