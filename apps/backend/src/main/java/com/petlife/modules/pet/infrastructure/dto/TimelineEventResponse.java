package com.petlife.modules.pet.infrastructure.dto;

import com.petlife.modules.pet.domain.entity.TimelineEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineEventResponse {
    private UUID id;
    private TimelineEventType type;
    private OffsetDateTime date;
    private String title;
    private String description;
    private String icon;
    private String color;
    private String photoUrl;
}
