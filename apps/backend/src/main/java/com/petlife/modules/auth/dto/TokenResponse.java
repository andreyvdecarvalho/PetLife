package com.petlife.modules.auth.dto;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {}
