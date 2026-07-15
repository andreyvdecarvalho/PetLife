package com.petlife.modules.veterinarian.domain.exception;

import com.petlife.shared.exception.BusinessException;
import org.springframework.http.HttpStatusCode;

public class InvalidCrmvException extends BusinessException {
    public InvalidCrmvException(String message) {
        super("INVALID_CRMV", message, HttpStatusCode.valueOf(422));
    }
}
