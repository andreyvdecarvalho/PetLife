package com.petlife.modules.veterinarian.infrastructure.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record SetVetScheduleRequest(
    @NotNull(message = "O dia da semana é obrigatório.")
    DayOfWeek dayOfWeek,
    @NotNull(message = "O horário de abertura é obrigatório.")
    LocalTime openTime,
    @NotNull(message = "O horário de fechamento é obrigatório.")
    LocalTime closeTime,
    boolean isActive
) {}
