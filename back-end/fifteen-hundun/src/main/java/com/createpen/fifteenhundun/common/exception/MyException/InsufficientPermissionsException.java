package com.createpen.fifteenhundun.common.exception.MyException;

public class InsufficientPermissionsException extends RuntimeException {
    public InsufficientPermissionsException(String message) {
        super(message);
    }
}
