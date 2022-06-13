package com.example.portfoliosOverview.exceptions.portfolioWithNameAlreadyExists;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class PortfolioWithNameAlreadyExistsAdvice {

    @ResponseBody
    @ExceptionHandler(PortfolioWithNameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String PortfolioWithNameAlreadyExistsHandler(PortfolioWithNameAlreadyExistsException ex) {
        return ex.getMessage();
    }

}
