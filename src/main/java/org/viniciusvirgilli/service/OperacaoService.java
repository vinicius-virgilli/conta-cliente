package org.viniciusvirgilli.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.viniciusvirgilli.dto.CreditoDebitoDto;
import org.viniciusvirgilli.enums.SituacaoContaEnum;
import org.viniciusvirgilli.enums.TipoOperacaoEnum;
import org.viniciusvirgilli.exception.ForaDoLimiteException;
import org.viniciusvirgilli.exception.SaldoNaoSuficienteException;
import org.viniciusvirgilli.model.Cliente;
import org.viniciusvirgilli.validador.CreditoDebitoValidador;
import org.viniciusvirgilli.util.DataUtil;

import java.math.BigDecimal;

@Slf4j
@ApplicationScoped
public class OperacaoService {

    @Inject
    CreditoDebitoValidador validador;

    @Inject
    ClienteService clienteService;

    @Transactional
    public void executar(CreditoDebitoDto dto) {
        validador.validar(dto);

        try {
            if (dto.getTipoOperacao() == TipoOperacaoEnum.CREDITO) {
                creditar(dto);
            } else if (dto.getTipoOperacao() == TipoOperacaoEnum.DEBITO) {
                debitar(dto);
            }
        } catch (Exception e) {
            log.error("[OPERACAO] - Erro ao executar operação: {}", dto, e);
            throw new RuntimeException("Erro ao executar operação", e);
        }
    }

    private void creditar(CreditoDebitoDto dto) {
        log.info("[OPERACAO] - Iniciando operação de crédito: {}", dto);
        
        try {
            Cliente cliente = clienteService.findByCpfCnpjAndTipoConta(dto.getCpfCnpj(), dto.getTipoConta());
            if (cliente.getSituacaoConta() == SituacaoContaEnum.ATIVA) {
                cliente.setSaldo(new BigDecimal(cliente.getSaldo().toString()).add(new BigDecimal(dto.getValor())));
                clienteService.atualizarCliente(cliente);
            } else {
                log.error("[OPERACAO] - Cliente não está ativo: {}", dto);
                throw new RuntimeException("Cliente não está ativo");
            }
        } catch (Exception e) {
            log.error("[OPERACAO] - Cliente não encontrado: {}", dto, e);
            throw new RuntimeException("Cliente não encontrado", e);
        }
    }

    private void debitar(CreditoDebitoDto dto) {
        log.info("[OPERACAO] - Iniciando operação de débito: {}", dto);
            Cliente cliente = clienteService.findByCpfCnpjAndTipoConta(dto.getCpfCnpj(), dto.getTipoConta());
            if (cliente.getSituacaoConta() == SituacaoContaEnum.ATIVA) {
                if (saldoNaoSuficiente(cliente, dto)) {
                    log.error("[OPERACAO] - Saldo insuficiente: {}", dto);
                    throw new SaldoNaoSuficienteException("Saldo insuficiente");
                }
                if (isLimitePixPermitido(cliente, dto)) {
                    cliente.setSaldo(new BigDecimal(cliente.getSaldo().toString()).subtract(new BigDecimal(dto.getValor())));
                    clienteService.atualizarCliente(cliente);
                } else {
                    log.error("[OPERACAO] - Limite de PIX não permitido: {}", dto);
                    throw new ForaDoLimiteException("Limite de PIX não permitido");
                }
            } else {
                log.error("[OPERACAO] - Cliente não está ativo: {}", dto);
                throw new RuntimeException("Cliente não está ativo");
            }
    }

    private boolean isLimitePixPermitido(Cliente cliente, CreditoDebitoDto dto) {
        if (dto.getConectadoEmRedeSegura()) {
            if (cliente.getLimitePixRedeSegura().compareTo(new BigDecimal(dto.getValor())) < 0) {
                return false;
            } else {
                return true;
            }
        }
        // precisamos verificar se é o período de dia ou o período de noite
        // para isso, vamos verificar se a hora está entre 06:00 e 20:00
        int hora = DataUtil.getHora(dto.getDataOperacao());
        if (hora < 6 || hora >= 20) {
            if (cliente.getLimitePixNoturno().compareTo(new BigDecimal(dto.getValor())) < 0) {
                return false;
            }
        } else {
            if (cliente.getLimitePixDiurno().compareTo(new BigDecimal(dto.getValor())) < 0) {
                return false;
            }
        }

        return true;
    }

    private boolean saldoNaoSuficiente(Cliente cliente, CreditoDebitoDto dto) {
        return cliente.getSaldo().compareTo(new BigDecimal(dto.getValor())) < 0;
    }
}
