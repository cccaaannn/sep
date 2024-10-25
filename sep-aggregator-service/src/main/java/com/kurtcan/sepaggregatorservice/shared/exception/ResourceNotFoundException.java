package com.kurtcan.sepaggregatorservice.shared.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {super("Not found");}
    public ResourceNotFoundException(String message) {super(message);}
}
