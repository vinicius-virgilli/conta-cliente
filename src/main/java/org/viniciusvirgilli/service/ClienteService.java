package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.viniciusvirgilli.dto.CadastroClienteDto;
import org.viniciusvirgilli.enums.TipoContaEnum;
import org.viniciusvirgilli.exception.ClienteJaCadastradoException;
import org.viniciusvirgilli.exception.ClienteNaoEncontradoException;
import org.viniciusvirgilli.model.Cliente;
import org.viniciusvirgilli.validador.CadastroClienteValidador;
import org.viniciusvirgilli.dao.ClienteDao;

import java.math.BigDecimal;
import java.util.Optional;


@Slf4j
@ApplicationScoped
public class ClienteService {

    @Inject
    CadastroClienteValidador cadastroClienteValidador;

    @Inject
    ClienteDao clienteDao;

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

    @Transactional
    public void alterarLimiteDiurno(String cpfCnpj, TipoContaEnum tipoConta, String limite) {
        BigDecimal limiteDiurno;
        try {
            limiteDiurno = new BigDecimal(limite);
        } catch (Exception e) {
            log.error("[ALTERAR] - Erro ao converter limite diurno para BigDecimal: {}", limite, e);
            throw new RuntimeException("Erro ao converter limite diurno para BigDecimal", e);
        }

        log.info("[ALTERAR] - Iniciando alteração do limite diurno do cliente por CPF/CNPJ e tipo de conta: {} - {}", cpfCnpj, tipoConta);
        try {
            Cliente cliente = findByCpfCnpjAndTipoConta(cpfCnpj, tipoConta);
            cliente.setLimitePixDiurno(limiteDiurno);
            clienteDao.persist(cliente);
            log.info("[ALTERAR] - Limite diurno alterado com sucesso: {} - {}", cpfCnpj, tipoConta);
        } catch (Exception e) {
            log.error("[ALTERAR] - Erro ao alterar limite diurno do cliente por CPF/CNPJ e tipo de conta: {} - {}", cpfCnpj, tipoConta, e);
            throw new RuntimeException("Erro ao alterar limite diurno do cliente por CPF/CNPJ e tipo de conta", e);
        }
    }

    @Transactional
    public void alterarLimiteNoturno(String cpfCnpj, TipoContaEnum tipoConta, String limite) {
        BigDecimal limiteNoturno;
        try {
            limiteNoturno = new BigDecimal(limite);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter limite para BigDecimal", e);
        }

        try {
            Cliente cliente = findByCpfCnpjAndTipoConta(cpfCnpj, tipoConta);
            cliente.setLimitePixNoturno(limiteNoturno);
            clienteDao.persist(cliente);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao alterar limite do cliente por CPF/CNPJ e tipo de conta", e);
        }
    }

    @Transactional
    public void alterarLimiteRedeSegura(String cpfCnpj, TipoContaEnum tipoConta, String limite) {
        BigDecimal limiteRedeSegura;
        try {
            limiteRedeSegura = new BigDecimal(limite);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter limite para BigDecimal", e);
        }

        try {
            Cliente cliente = findByCpfCnpjAndTipoConta(cpfCnpj, tipoConta);
            cliente.setLimitePixRedeSegura(limiteRedeSegura);
            clienteDao.persist(cliente);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao alterar limite do cliente por CPF/CNPJ e tipo de conta", e);
        }
    }

    private Cliente toEntity(CadastroClienteDto cliente) {
        Cliente entity = new Cliente();
        entity.setNome(cliente.getNome());
        entity.setCpfCnpj(cliente.getCpfCnpj());
        entity.setSaldo(new BigDecimal(cliente.getSaldo()));
        entity.setAgencia(cliente.getAgencia());
        entity.setConta(cliente.getConta());
        entity.setTipoConta(cliente.getTipoConta());
        entity.setOperacao(cliente.getOperacao());
        entity.setIspbParticipante(cliente.getIspbParticipante());
        entity.setSituacaoConta(cliente.getSituacaoConta());
        entity.setLimitePixDiurno(cliente.getLimitePixDiurno());
        entity.setLimitePixNoturno(cliente.getLimitePixNoturno());
        entity.setLimitePixRedeSegura(cliente.getLimitePixRedeSegura());
        return entity;
    }
}
