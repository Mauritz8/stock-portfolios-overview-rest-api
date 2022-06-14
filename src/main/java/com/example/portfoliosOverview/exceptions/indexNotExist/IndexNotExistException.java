package com.example.portfoliosOverview.exceptions.indexNotExist;

public class IndexNotExistException extends RuntimeException {

    public IndexNotExistException(String name) {
        super("There is no index with the name " + name);
    }
}
