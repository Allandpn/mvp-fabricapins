package com.finalphase.fabricapins.exception.model;

import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends CustomError{
    @Getter
    private List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(Instant timestamp, Integer status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
    }
    public void addError(String fieldName, String message){
        errors.add( new FieldMessage(fieldName, message));
    }
}
