package com.example.portfoliosOverview.exceptions.stockNotExist;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class StockNotExistAdvice {

    @ResponseBody
    @ExceptionHandler(StockNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String StockNotExistHandler(StockNotExistException ex) {
        return ex.getMessage();
    }
}
