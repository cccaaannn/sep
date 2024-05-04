package com.kurtcan.sepproductservice.shared.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {
    public <T>ResponseEntity<T> ok(T data) {
        return ResponseEntity.ok(data);
    }

    public <T>ResponseEntity<T> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }
}
