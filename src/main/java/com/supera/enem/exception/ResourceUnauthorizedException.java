package com.supera.enem.exception;


public class ResourceUnauthorizedException extends RuntimeException {
    public ResourceUnauthorizedException(String message) {
        super(message);
    }
}
