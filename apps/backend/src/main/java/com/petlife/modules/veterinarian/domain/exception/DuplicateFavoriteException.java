package com.petlife.modules.veterinarian.domain.exception;

import com.petlife.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateFavoriteException extends BusinessException {
    public DuplicateFavoriteException(String message) {
        super("DUPLICATE_FAVORITE", message, HttpStatus.CONFLICT);
    }
}
