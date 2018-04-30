package com.insys.icom.demo.bernard.commons.exceptions;

@FunctionalInterface
public interface ThrowingTechnicalException<T, E extends Exception> {
    void accept(T t) throws E;
}
