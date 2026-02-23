package com.finalphase.fabricapins.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class CustomError {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
