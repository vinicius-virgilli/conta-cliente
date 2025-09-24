package org.viniciusvirgilli.exception;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class ClienteJaCadastradoException extends RuntimeException {
    private final int codigoHTTP = Response.Status.CONFLICT.getStatusCode();

    public ClienteJaCadastradoException() {
        super("Cliente já cadastrado!");
    }

}
