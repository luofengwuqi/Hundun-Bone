package com.createpen.fifteenhundun.common.exception.MyException;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
