package com.petlife.modules.veterinarian.domain.exception;

import com.petlife.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class VeterinarianNotFoundException extends BusinessException {
    public VeterinarianNotFoundException(String message) {
        super("VET_NOT_FOUND", message, HttpStatus.NOT_FOUND);
    }
}
