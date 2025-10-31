package com.flower.shop.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DuplicateKeyException;
import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler({DuplicateKeyException.class, SQLIntegrityConstraintViolationException.class})
    public ResponseEntity<String> handleDuplicateKey(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT).body("已存在相同花名");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception ex) {
        ex.printStackTrace(); // 打印到控制台，便于定位
        String msg = ex.getClass().getName() + ": " + (ex.getMessage() == null ? "" : ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
    }
}