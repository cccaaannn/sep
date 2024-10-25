package com.kurtcan.sepaggregatorservice.shared.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException() {super("Already exists");}
    public ResourceAlreadyExistsException(String message) {super(message);}
}
