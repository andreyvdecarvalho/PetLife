package com.petlife.shared.response;

public record ApiResponse<T>(
    T data,
    PageMeta meta
) {
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> paged(T data, PageMeta meta) {
        return new ApiResponse<>(data, meta);
    }
}
