package com.petlife.modules.notification.domain.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class NotificationPreferences {

    private UUID userId;

    private boolean pushEnabled = true;

    private boolean emailEnabled = true;

    private boolean vaccines = true;

    private boolean medications = true;

    private boolean appointments = true;

    private boolean grooming = true;

    private boolean marketing = false;

    private LocalTime doNotDisturbStart = LocalTime.of(22, 0);

    private LocalTime doNotDisturbEnd = LocalTime.of(7, 0);
}
