package com.food.ordering.system.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDTO handleException(Exception e){
        log.error(e.getMessage(), e);
        return new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unexpected error!");

    }
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorDTO handleException(ValidationException e){
       ErrorDTO errorDTO;
       if(e instanceof ConstraintViolationException){
           String violations = extractViolationsFromException((ConstraintViolationException) e);
           log.error(violations, e);
           errorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST.getReasonPhrase(), violations);
       }else {
           var exceptionMessage = e.getMessage();
           log.error(exceptionMessage, e);
           errorDTO = new ErrorDTO(HttpStatus.BAD_REQUEST.getReasonPhrase(), exceptionMessage);
       }
       return errorDTO;
    }

    private String extractViolationsFromException(ConstraintViolationException e) {
      return e.getConstraintViolations()
              .stream()
              .map(ConstraintViolation::getMessage)
              .collect(Collectors.joining("--"));
    }
}
