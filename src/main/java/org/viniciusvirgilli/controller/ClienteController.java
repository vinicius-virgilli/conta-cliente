package org.viniciusvirgilli.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;
import org.viniciusvirgilli.dto.CadastroClienteDto;
import org.viniciusvirgilli.dto.CreditoDebitoDto;
import org.viniciusvirgilli.enums.TipoContaEnum;
import org.viniciusvirgilli.model.Cliente;
import org.viniciusvirgilli.service.ClienteService;
import org.viniciusvirgilli.service.OperacaoService;


@Path("api/clientes")
@Slf4j
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Conta", description = "Operações na conta do cliente")
public class ClienteController {

    @Inject
    private ClienteService contaService;

    @Inject
    OperacaoService operacaoService;

    @POST
    @Path("/cadastrar")
    public Response cadastrar(CadastroClienteDto cliente) {
        Cliente clienteCadastrado = contaService.cadastrar(cliente);
        return Response.status(Response.Status.CREATED).entity(clienteCadastrado).build();
    }

    @GET
    @Path("/por-cpfCnpj-tipoConta")
    public Response buscar(
            @QueryParam("cpfCnpj") String cpfCnpj,
            @QueryParam("tipoConta") TipoContaEnum tipoConta
            ) {
        Cliente cliente = contaService.findByCpfCnpjAndTipoConta(cpfCnpj, tipoConta);
        return Response.ok(cliente).build();
    }

    @GET
    @Path("/por-contaId")
    public Response buscarPorId(@QueryParam("contaId") Long contaId) {
        Cliente cliente = contaService.findById(contaId);
        return Response.ok(cliente).build();
    }

    @DELETE
    @Path("/")
    public Response deletar(
            @QueryParam("cpfCnpj") String cpfCnpj,
            @QueryParam("tipoConta") TipoContaEnum tipoConta
    ) {
        contaService.deletar(cpfCnpj, tipoConta);
        return Response.noContent().build();
    }

    @PUT
    @Path("/operacao")
    public Response creditar(CreditoDebitoDto creditoDto) {
        operacaoService.executar(creditoDto);
        return Response.noContent().build();
    }

}
