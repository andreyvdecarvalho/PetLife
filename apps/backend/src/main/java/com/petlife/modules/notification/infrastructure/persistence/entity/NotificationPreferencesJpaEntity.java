package com.petlife.modules.notification.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notification_preferences")
public class NotificationPreferencesJpaEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "push_enabled")
    private boolean pushEnabled = true;

    @Column(name = "email_enabled")
    private boolean emailEnabled = true;

    @Column(name = "vaccines")
    private boolean vaccines = true;

    @Column(name = "medications")
    private boolean medications = true;

    @Column(name = "appointments")
    private boolean appointments = true;

    @Column(name = "grooming")
    private boolean grooming = true;

    @Column(name = "marketing")
    private boolean marketing = false;

    @Column(name = "do_not_disturb_start")
    private LocalTime doNotDisturbStart = LocalTime.of(22, 0);

    @Column(name = "do_not_disturb_end")
    private LocalTime doNotDisturbEnd = LocalTime.of(7, 0);
}
