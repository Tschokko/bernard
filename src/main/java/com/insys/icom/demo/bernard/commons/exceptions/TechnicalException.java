package com.insys.icom.demo.bernard.commons.exceptions;

/**
 * @author Lars Gielsok, MaibornWolff GmbH
 */
public class TechnicalException  extends RuntimeException {
    public TechnicalException() { }

    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
