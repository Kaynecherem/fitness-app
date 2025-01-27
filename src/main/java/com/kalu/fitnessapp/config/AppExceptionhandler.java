//package com.kalu.fitnessapp.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@Slf4j
//@Component
//@RestControllerAdvice
//public class AppExceptionhandler {
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleException(Exception e) {
//        log.error("An error occurred: {}", e.getMessage(), e);
//        return ResponseEntity.badRequest().body(e.getMessage());
//    }
//}
