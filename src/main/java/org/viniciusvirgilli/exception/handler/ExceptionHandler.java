package org.viniciusvirgilli.exception.handler;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.viniciusvirgilli.exception.ClienteJaCadastradoException;
import org.viniciusvirgilli.exception.ClienteNaoEncontradoException;
import org.viniciusvirgilli.exception.dto.ErroDetailCamposDto;
import org.viniciusvirgilli.exception.dto.ErroDetailDto;
import org.viniciusvirgilli.exception.ValidadorException;

import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.Date;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        Throwable cause = getCause(e);

        if (cause instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErroDetailDto.builder()
                            .message(cause.getMessage())
                            .status(Response.Status.BAD_REQUEST.getStatusCode())
                            .timestamp(new Date())
                            .build())
                    .build();
        }

        if (cause instanceof ValidadorException validadorException) {
            return Response.status(validadorException.getCodigoHTTP())
                    .entity(ErroDetailCamposDto.builder()
                            .message(validadorException.getMessage())
                            .status(validadorException.getCodigoHTTP())
                            .timestamp(new Date())
                            .camposComProblemasDto(validadorException.getCamposComProblemas())
                            .build())
                    .build();

        }

        if (cause instanceof ClienteJaCadastradoException cje) {
            return Response.status(cje.getCodigoHTTP())
                    .entity(ErroDetailDto.builder()
                            .message(cje.getMessage())
                            .status(cje.getCodigoHTTP())
                            .timestamp(new Date())
                            .build())
                    .build();
        }

        if (cause instanceof ClienteNaoEncontradoException cje) {
            return Response.status(cje.getStatus())
                    .entity(ErroDetailDto.builder()
                            .message(cje.getMessage())
                            .status(cje.getStatus())
                            .timestamp(new Date())
                            .build())
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErroDetailDto.builder()
                        .message(e.getMessage())
                        .status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                        .timestamp(new Date())
                        .build())
                .build();

    }

    private Throwable getCause(Throwable throwable) {
        Throwable cause;
        while ((cause = throwable.getCause()) != null) {
            throwable = cause;
        }
        return throwable;
    }
}
