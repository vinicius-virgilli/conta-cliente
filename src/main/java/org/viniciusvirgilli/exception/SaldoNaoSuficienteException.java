package org.viniciusvirgilli.exception;

import lombok.Getter;

@Getter
public class SaldoNaoSuficienteException extends RuntimeException {
    private final int status = 422;

    public SaldoNaoSuficienteException(String message) {
        super(message);
    }
}
