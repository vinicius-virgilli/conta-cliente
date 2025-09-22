package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.viniciusvirgilli.dto.CadastroClienteDto;
import org.viniciusvirgilli.model.Cliente;
import org.viniciusvirgilli.validador.CadastroClienteValidador;
import org.viniciusvirgilli.dao.ClienteDao;
import java.util.Optional;


@Slf4j
@ApplicationScoped
public class ClienteService {

    @Inject
    private CadastroClienteValidador validador;

    @Inject
    private ClienteDao clienteDao;

    public Cliente cadastrar(CadastroClienteDto cliente) {
        log.info("[CADASTRO] - Iniciando cadastro do cliente: {}", cliente);

        validador.validar(cliente);

        Cliente entity = new Cliente();
        entity.setNome(cliente.getNome());
        entity.setCpfCnpj(cliente.getCpfCnpj());
        entity.setSaldo(cliente.getSaldo());
        entity.setAgencia(cliente.getAgencia());
        entity.setConta(cliente.getConta());
        entity.setTipoConta(cliente.getTipoConta());
        entity.setOperacao(cliente.getOperacao());
        entity.setIspbParticipante(cliente.getIspbParticipante());

        clienteDao.persist(entity);

        log.info("[CADASTRO] - Cliente cadastrado com sucesso: {}", cliente);
        return entity;
    }


    public Optional<Cliente> findByCpfCnpj(String cpfCnpj) {
        log.info("[BUSCA] - Iniciando busca do cliente por CPF/CNPJ: {}", cpfCnpj);
        try {
            Optional<Cliente> cliente = clienteDao.findByCpfCnpj(cpfCnpj);
            if (cliente.isPresent()) {
                log.info("[BUSCA] - Cliente encontrado por CPF/CNPJ: {}", cpfCnpj);
            } else {
                log.info("[BUSCA] - Nenhum cliente encontrado por CPF/CNPJ: {}", cpfCnpj);
                throw new RuntimeException("Nenhum cliente encontrado por CPF/CNPJ");
            }
            return cliente;
        } catch (Exception e) {
            log.error("[BUSCA] - Erro ao buscar cliente por CPF/CNPJ: {}", cpfCnpj, e);
            throw new RuntimeException("Erro ao buscar cliente por CPF/CNPJ", e);
        }
    }
}
