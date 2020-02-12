package com.angaza.nexus.keycode.exceptions;


public class BaseException extends Exception {
    public BaseException() {
        super();
    }

    public BaseException(String detailMessage) {
        super(detailMessage);
    }

    public BaseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BaseException(Throwable throwable) {
        super(throwable);
    }
}
