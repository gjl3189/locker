package com.cyou.cma.clockscreen.core;

public class ClassInitException extends Exception {

    private static final long serialVersionUID = -6830755761817085197L;

    public ClassInitException() {
    }

    public ClassInitException(String detailMessage) {
        super(detailMessage);
    }

    public ClassInitException(Throwable throwable) {
        super(throwable);
    }

    public ClassInitException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
