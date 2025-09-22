package org.viniciusvirgilli.exception;

import lombok.Getter;
import org.viniciusvirgilli.exception.dto.CamposComProblemasDto;
import jakarta.ws.rs.core.Response;

@Getter
public class ValidadorException extends RuntimeException {

    private int codigoHTTP = Response.Status.BAD_REQUEST.getStatusCode();
    private CamposComProblemasDto camposComProblemas;

    public ValidadorException(CamposComProblemasDto camposComProblemas) {
        super("Campo(s) com problema(s)");
        this.camposComProblemas = camposComProblemas;
    }
}
