package com.palest.ink.ons.serialize;

/**
 * @ fileName: SerializationException.java
 * @ author: WeiHui
 * @ date: 2018/1/23 16:39
 * @ version: v1.0.0
 */
public class SerializationException extends RuntimeException {

    public SerializationException() {
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
