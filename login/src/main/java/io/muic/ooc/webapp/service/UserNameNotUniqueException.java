package io.muic.ooc.webapp.service;

public class UserNameNotUniqueException extends UserServiceException {

    public UserNameNotUniqueException(String message) {
        super(message);
    }

}

