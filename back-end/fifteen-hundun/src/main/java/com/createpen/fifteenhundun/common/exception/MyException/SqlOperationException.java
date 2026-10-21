package com.createpen.fifteenhundun.common.exception.MyException;

public class SqlOperationException extends RuntimeException {
    public SqlOperationException(String message) {
        super(message);
    }
}
