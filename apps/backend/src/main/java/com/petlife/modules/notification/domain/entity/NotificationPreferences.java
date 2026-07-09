package com.petlife.modules.notification.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
public class NotificationPreferences {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "push_enabled", nullable = false)
    private boolean pushEnabled = true;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled = true;

    @Column(name = "vaccines", nullable = false)
    private boolean vaccines = true;

    @Column(name = "medications", nullable = false)
    private boolean medications = true;

    @Column(name = "appointments", nullable = false)
    private boolean appointments = true;

    @Column(name = "grooming", nullable = false)
    private boolean grooming = true;

    @Column(name = "marketing", nullable = false)
    private boolean marketing = false;

    @Column(name = "do_not_disturb_start", nullable = false)
    private LocalTime doNotDisturbStart = LocalTime.of(22, 0);

    @Column(name = "do_not_disturb_end", nullable = false)
    private LocalTime doNotDisturbEnd = LocalTime.of(7, 0);
}
