package org.viniciusvirgilli.exception;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class ClienteNaoEncontradoException extends RuntimeException {
    private final int status = Response.Status.NOT_FOUND.getStatusCode();

    public ClienteNaoEncontradoException() {
        super("Cliente não encontrado");
    }
}
