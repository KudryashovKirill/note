package com.example.note.demo.util.exception;

public class ObjectAlreadyInTableException extends RuntimeException {
    public ObjectAlreadyInTableException(String message) {
        super(message);
    }
}
