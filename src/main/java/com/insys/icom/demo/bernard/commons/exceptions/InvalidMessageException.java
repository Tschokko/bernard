package com.insys.icom.demo.bernard.commons.exceptions;

/**
 * @author Lars Gielsok, MaibornWolff GmbH
 */
public class InvalidMessageException extends TechnicalException {
    public InvalidMessageException() { }

    public InvalidMessageException(String message) {
        super(message);
    }

    public InvalidMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
