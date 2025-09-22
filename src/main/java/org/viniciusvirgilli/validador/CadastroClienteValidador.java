package org.viniciusvirgilli.validador;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import org.viniciusvirgilli.dto.CadastroClienteDto;
import org.viniciusvirgilli.exception.ValidadorException;
import org.viniciusvirgilli.exception.dto.CamposComProblemasDto;
import org.viniciusvirgilli.model.Cliente;
import org.viniciusvirgilli.util.MessageUtils;
import org.viniciusvirgilli.enums.ISPBParticipanteEnum;
import org.viniciusvirgilli.enums.TipoContaEnum;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class CadastroClienteValidador {

    public void validar(CadastroClienteDto cliente) {
        
        List<String> campos = new ArrayList<>();

        validarNome(cliente.getNome(), campos);
        validarCpfCnpj(cliente.getCpfCnpj(), campos);
        validarSaldo(cliente.getSaldo(), campos);
        validarAgencia(cliente.getAgencia(), campos);
        validarConta(cliente.getConta(), campos);
        validarTipoConta(cliente.getTipoConta(), campos);
        validarOperacao(cliente.getOperacao(), campos);
        validarISPBParticipante(cliente.getIspbParticipante(), campos);

        if (!campos.isEmpty()) {
            throw new ValidadorException(new CamposComProblemasDto(campos));
        }
    }

    private void validarNome(String nome, List<String> campos) {
        if (nome == null || nome.isBlank()) {
            campos.add(MessageUtils.getString("cliente.nome.obrigatorio"));
        }
    }

    private void validarCpfCnpj(String cpfCnpj, List<String> campos) {
        if (cpfCnpj == null || cpfCnpj.isBlank()) {
            campos.add(MessageUtils.getString("cliente.cpfcnpj.obrigatorio"));
        } else if (!cpfCnpj.matches("\\d{11}|\\d{14}")) {
            campos.add(MessageUtils.getString("cliente.cpfcnpj.invalido"));
        }
    }

    private void validarSaldo(BigDecimal saldo, List<String> campos) {
        if (saldo == null) {
            campos.add(MessageUtils.getString("cliente.saldo.obrigatorio"));
        } else if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
            campos.add(MessageUtils.getString("cliente.saldo.invalido"));
        }
    }

    private void validarAgencia(String agencia, List<String> campos) {
        if (agencia == null || agencia.isBlank()) {
            campos.add(MessageUtils.getString("cliente.agencia.obrigatorio"));
        }
    }

    private void validarConta(Integer conta, List<String> campos) {
        if (conta == null) {
            campos.add(MessageUtils.getString("cliente.conta.obrigatorio"));
        } else if (conta < 0) {
            campos.add(MessageUtils.getString("cliente.conta.invalido"));
        }

    }

    private void validarOperacao(Integer operacao, List<String> campos) {
        if (operacao == null) {
            campos.add(MessageUtils.getString("cliente.operacao.obrigatorio"));
        }
    }

    private void validarISPBParticipante(ISPBParticipanteEnum ispbParticipante, List<String> campos) {
        if (ispbParticipante == null) {
            campos.add(MessageUtils.getString("cliente.ispbparticipante.obrigatorio"));
        } else if (ispbParticipante == ISPBParticipanteEnum.CX || ispbParticipante == ISPBParticipanteEnum.IF) {

            campos.add(MessageUtils.getString("cliente.ispbparticipante.invalido"));
        }
    }

    private void validarTipoConta(TipoContaEnum tipoConta, List<String> campos) {
        if (tipoConta == null) {
            campos.add(MessageUtils.getString("cliente.tipoconta.obrigatorio"));
        } else if (!(tipoConta == TipoContaEnum.CACC || tipoConta == TipoContaEnum.SVGS)) {
            campos.add(MessageUtils.getString("cliente.tipoconta.invalido"));
        }
    }
}
