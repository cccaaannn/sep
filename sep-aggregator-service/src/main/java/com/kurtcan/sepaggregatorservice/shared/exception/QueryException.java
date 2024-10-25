package com.kurtcan.sepaggregatorservice.shared.exception;


import lombok.Getter;
import org.springframework.graphql.execution.ErrorType;

@Getter
public class QueryException extends RuntimeException {

    private ErrorType errorType = ErrorType.BAD_REQUEST;

    public QueryException() {
        super("Query error");
    }

    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
}
