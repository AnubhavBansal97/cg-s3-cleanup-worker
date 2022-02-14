package com.exceptions;

public class BmsClientException extends RuntimeException{
    public BmsClientException(final String msg) {
        super(msg);
    }

    public BmsClientException(final String msg, final Throwable t) {
        super(msg, t);
    }
}
