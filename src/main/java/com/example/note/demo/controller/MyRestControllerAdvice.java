package com.example.note.demo.controller;

import com.example.note.demo.util.exception.ErrorResponse;
import com.example.note.demo.util.exception.NoDataFoundException;
import com.example.note.demo.util.exception.ObjectAlreadyInTableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyRestControllerAdvice {
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<ErrorResponse> noDataFoundException(NoDataFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getClass().getName(), exception.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectAlreadyInTableException.class)
    public ResponseEntity<ErrorResponse> objectAlreadyInTableException(ObjectAlreadyInTableException exception) {
        return new ResponseEntity<>(new ErrorResponse(exception.getClass().getName(), exception.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
