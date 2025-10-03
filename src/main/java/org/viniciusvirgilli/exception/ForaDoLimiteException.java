package org.viniciusvirgilli.exception;

import lombok.Getter;

@Getter
public class ForaDoLimiteException extends RuntimeException {
    private final int status = 422;

    public ForaDoLimiteException(String message) {
        super(message);
    }
}
