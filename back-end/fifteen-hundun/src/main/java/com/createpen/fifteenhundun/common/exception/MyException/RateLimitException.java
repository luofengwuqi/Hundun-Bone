package com.createpen.fifteenhundun.common.exception.MyException;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}
