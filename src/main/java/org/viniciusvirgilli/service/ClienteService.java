package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.viniciusvirgilli.dto.CadastroClienteDto;
import org.viniciusvirgilli.dto.CreditoDto;
import org.viniciusvirgilli.dto.DebitoDto;
import org.viniciusvirgilli.enums.TipoContaEnum;
import org.viniciusvirgilli.exception.ClienteJaCadastradoException;
import org.viniciusvirgilli.exception.ClienteNaoEncontradoException;
import org.viniciusvirgilli.model.Cliente;
import org.viniciusvirgilli.validador.CadastroClienteValidador;
import org.viniciusvirgilli.dao.ClienteDao;
import org.viniciusvirgilli.validador.CreditoDebitoValidador;

import java.util.Optional;


@Slf4j
@ApplicationScoped
public class ClienteService {

    @Inject
    private CadastroClienteValidador cadastroClienteValidador;

    @Inject
    private CreditoDebitoValidador creditoDebitoValidador;

    @Inject
    private ClienteDao clienteDao;

    @Transactional
    public Cliente cadastrar(CadastroClienteDto cliente) {
        log.info("[CADASTRO] - Iniciando cadastro do cliente: {}", cliente);

        cadastroClienteValidador.validar(cliente);

        if (clienteJaCadrastrado(cliente.getCpfCnpj(), cliente.getTipoConta())) {
            log.info("[CADASTRO] - Cliente já cadastrado: {}", cliente);
            throw new ClienteJaCadastradoException();
        }

        try {
            Cliente entity = toEntity(cliente);

            clienteDao.persist(entity);
            log.info("[CADASTRO] - Cliente cadastrado com sucesso: {}", cliente);
            return entity;
        } catch (Exception e) {
            log.error("[CADASTRO] - Erro ao cadastrar cliente: {}", cliente, e);
            throw new RuntimeException("Erro ao cadastrar cliente", e);
        }
    }

    public Cliente findById(Long id) {
        log.info("[BUSCA] - Iniciando busca do cliente por ID: {}", id);
        try {
            Optional<Cliente> cliente = clienteDao.findByIdOptional(id);
            if (cliente.isPresent()) {
                log.info("[BUSCA] - Cliente encontrado por ID: {}", id);
            } else {
                log.info("[BUSCA] - Nenhum cliente encontrado por ID: {}", id);
                throw new ClienteNaoEncontradoException();
            }
            return cliente.get();
        } catch (Exception e) {
            log.error("[BUSCA] - Erro ao buscar cliente por ID: {}", id, e);
            throw new RuntimeException("Erro ao buscar cliente por ID", e);
        }
    }

    public Cliente findByCpfCnpjAndTipoConta(String cpfCnpj, TipoContaEnum tipoConta) {
        log.info("[BUSCA] - Iniciando busca do cliente por CPF/CNPJ e tipo de conta: {} - {}", cpfCnpj, tipoConta);
        try {
            Optional<Cliente> cliente = clienteDao.findByCpfCnpjAndTipoConta(cpfCnpj, tipoConta);
            if (cliente.isPresent()) {
                log.info("[BUSCA] - Cliente encontrado por CPF/CNPJ e tipo de conta: {} - {}", cpfCnpj, tipoConta);
            } else {
                log.info("[BUSCA] - Nenhum cliente encontrado por CPF/CNPJ e tipo de conta: {} - {}", cpfCnpj, tipoConta);
                throw new ClienteNaoEncontradoException();
            }
            return cliente.get();
        } catch (Exception e) {
            log.error("[BUSCA] - Erro ao buscar cliente por CPF/CNPJ e tipo de conta: {} - {}", cpfCnpj, tipoConta, e);
            throw new RuntimeException("Erro ao buscar cliente por CPF/CNPJ e tipo de conta", e);
        }
    }

    @Transactional
    public void deletar(String cpfCnpj, TipoContaEnum tipoConta) {
        log.info("[DELETAR] - Iniciando deleção do cliente por CPF/CNPJ e tipo de conta: {} - {}", cpfCnpj, tipoConta);
        try {
            Optional<Cliente> cliente = clienteDao.findByCpfCnpjAndTipoConta(cpfCnpj, tipoConta);
            if (cliente.isPresent()) {
                clienteDao.delete(cliente.get());
                log.info("[DELETAR] - Cliente deletado com sucesso: {} - {}", cpfCnpj, tipoConta);
            } else {
                log.info("[DELETAR] - Nenhum cliente encontrado para deleção: {} - {}", cpfCnpj, tipoConta);
                throw new ClienteNaoEncontradoException();
            }
        } catch (Exception e) {
            log.error("[DELETAR] - Erro ao deletar cliente por CPF/CNPJ e tipo de conta: {} - {}", cpfCnpj, tipoConta, e);
            throw new RuntimeException("Erro ao deletar cliente por CPF/CNPJ e tipo de conta", e);
        }
    }

    private boolean clienteJaCadrastrado(String cpfCnpj, TipoContaEnum tipoConta) {
        return clienteDao.jaExisteConta(cpfCnpj, tipoConta);
    }

    private Cliente toEntity(CadastroClienteDto cliente) {
        Cliente entity = new Cliente();
        entity.setNome(cliente.getNome());
        entity.setCpfCnpj(cliente.getCpfCnpj());
        entity.setSaldo(cliente.getSaldo());
        entity.setAgencia(cliente.getAgencia());
        entity.setConta(cliente.getConta());
        entity.setTipoConta(cliente.getTipoConta());
        entity.setOperacao(cliente.getOperacao());
        entity.setIspbParticipante(cliente.getIspbParticipante());
        return entity;
    }

    @Transactional
    public void creditar(CreditoDto creditoDto) {
        log.info("[CREDITAR] - Iniciando crédito do cliente: {}", creditoDto);
        creditoDebitoValidador.validar(creditoDto.getCpfCnpj(), creditoDto.getTipoConta(), creditoDto.getValorCredito());

        try {
            Optional<Cliente> cliente = clienteDao.findByCpfCnpjAndTipoConta(creditoDto.getCpfCnpj(), creditoDto.getTipoConta());
            if (cliente.isPresent()) {
                cliente.get().setSaldo(cliente.get().getSaldo().add(creditoDto.getValorCredito()));
                clienteDao.persist(cliente.get());
                log.info("[CREDITAR] - Crédito do cliente realizado com sucesso: {}", creditoDto);
            } else {
                log.info("[CREDITAR] - Nenhum cliente encontrado para crédito: {}", creditoDto);
                throw new ClienteNaoEncontradoException();
            }
        } catch (Exception e) {
            log.error("[CREDITAR] - Erro ao creditar cliente: {}", creditoDto, e);
            throw new RuntimeException("Erro ao creditar cliente", e);
        }
    }

    @Transactional
    public void debitar(DebitoDto debitoDto) {
        log.info("[CREDITAR] - Iniciando crédito do cliente: {}", debitoDto);
        creditoDebitoValidador.validar(debitoDto.getCpfCnpj(), debitoDto.getTipoConta(), debitoDto.getValorDebito());

        try {
            Optional<Cliente> cliente = clienteDao.findByCpfCnpjAndTipoConta(debitoDto.getCpfCnpj(), debitoDto.getTipoConta());
            if (cliente.isPresent()) {
                cliente.get().setSaldo(cliente.get().getSaldo().subtract(debitoDto.getValorDebito()));
                clienteDao.persist(cliente.get());
                log.info("[CREDITAR] - Crédito do cliente realizado com sucesso: {}", debitoDto);
            } else {
                log.info("[CREDITAR] - Nenhum cliente encontrado para crédito: {}", debitoDto);
                throw new ClienteNaoEncontradoException();
            }
        } catch (Exception e) {
            log.error("[CREDITAR] - Erro ao creditar cliente: {}", debitoDto, e);
            throw new RuntimeException("Erro ao creditar cliente", e);
        }
    }
}
