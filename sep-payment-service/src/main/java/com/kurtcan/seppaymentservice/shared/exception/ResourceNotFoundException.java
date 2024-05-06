package com.kurtcan.seppaymentservice.shared.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {super("Not found");}
    public ResourceNotFoundException(String message) {super(message);}
}
