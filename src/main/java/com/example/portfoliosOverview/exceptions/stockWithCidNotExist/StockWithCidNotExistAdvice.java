package com.example.portfoliosOverview.exceptions.stockWithCidNotExist;

import com.example.portfoliosOverview.exceptions.stockNotExist.StockNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class StockWithCidNotExistAdvice {

    @ResponseBody
    @ExceptionHandler(StockWithCidNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String StockWithCidNotExistHandler(StockWithCidNotExistException ex) {
        return ex.getMessage();
    }
}
