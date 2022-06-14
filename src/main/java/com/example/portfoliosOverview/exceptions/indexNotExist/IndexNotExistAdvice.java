package com.example.portfoliosOverview.exceptions.indexNotExist;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class IndexNotExistAdvice {

    @ResponseBody
    @ExceptionHandler(IndexNotExistException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String IndexNotExistHandler(IndexNotExistException ex) {
        return ex.getMessage();
    }
}
