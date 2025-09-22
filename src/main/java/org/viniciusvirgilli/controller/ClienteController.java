package org.viniciusvirgilli.controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;
import org.viniciusvirgilli.dto.CadastroClienteDto;
import org.viniciusvirgilli.model.Cliente;
import org.viniciusvirgilli.service.ClienteService;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.GET;
import java.util.Optional;
import jakarta.ws.rs.PathParam;


@Path("api/cliente")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Conta", description = "Operações na conta do cliente")
public class ClienteController {

    @Inject
    private ClienteService contaService;

    @POST
    @Path("/cadastrar")
    public Cliente cadastrar(CadastroClienteDto cliente) {

        return contaService.cadastrar(cliente);
    }

    @GET
    @Path("/{cpfCnpj}")
    public Optional<Cliente> buscar(@PathParam("cpfCnpj") String cpfCnpj) {
        return contaService.findByCpfCnpj(cpfCnpj);
    }

}
