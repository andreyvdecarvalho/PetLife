package com.petlife.modules.veterinarian.domain.exception;

import com.petlife.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CrmvAlreadyExistsException extends BusinessException {
    public CrmvAlreadyExistsException(String message) {
        super("CRMV_ALREADY_EXISTS", message, HttpStatus.CONFLICT);
    }
}
