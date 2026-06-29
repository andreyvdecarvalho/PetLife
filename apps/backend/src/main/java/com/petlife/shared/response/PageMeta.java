package com.petlife.shared.response;

public record PageMeta(
    int page,
    int perPage,
    long total,
    int totalPages
) {}
