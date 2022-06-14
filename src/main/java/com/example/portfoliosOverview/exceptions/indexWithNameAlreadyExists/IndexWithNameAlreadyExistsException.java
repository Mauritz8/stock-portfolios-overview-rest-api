package com.example.portfoliosOverview.exceptions.indexWithNameAlreadyExists;

public class IndexWithNameAlreadyExistsException extends RuntimeException {

    public IndexWithNameAlreadyExistsException(String name) {
        super("You have already added the index " + name);
    }
}
