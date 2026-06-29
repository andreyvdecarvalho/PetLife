package com.petlife.shared.response;

import java.util.List;

public record ErrorResponse(
    ErrorDetail error
) {
    public record ErrorDetail(
        String code,
        String message,
        List<FieldError> details
    ) {}

    public record FieldError(
        String field,
        String message
    ) {}
}
