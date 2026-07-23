package com.petlife.modules.auth.application.dto;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {}
