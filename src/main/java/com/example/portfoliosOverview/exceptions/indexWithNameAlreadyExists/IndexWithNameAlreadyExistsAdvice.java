package com.example.portfoliosOverview.exceptions.indexWithNameAlreadyExists;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class IndexWithNameAlreadyExistsAdvice {

    @ResponseBody
    @ExceptionHandler(IndexWithNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String IndexWithNameAlreadyExistsHandler(IndexWithNameAlreadyExistsException ex) {
        return ex.getMessage();
    }
}
