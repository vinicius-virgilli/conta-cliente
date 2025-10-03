package org.viniciusvirgilli.validador;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import io.netty.util.internal.StringUtil;
import lombok.NoArgsConstructor;
import org.viniciusvirgilli.dto.CadastroClienteDto;
import org.viniciusvirgilli.enums.SituacaoContaEnum;
import org.viniciusvirgilli.exception.ValidadorException;
import org.viniciusvirgilli.exception.dto.CamposComProblemasDto;
import org.viniciusvirgilli.model.Cliente;
import org.viniciusvirgilli.util.MessageUtils;
import org.viniciusvirgilli.enums.ISPBParticipanteEnum;
import org.viniciusvirgilli.enums.TipoContaEnum;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@ApplicationScoped
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
        validarSituacaoConta(cliente.getSituacaoConta(), campos);
        validarLimites(cliente.getLimitePixDiurno(), cliente.getLimitePixNoturno(), cliente.getLimitePixRedeSegura() ,campos);

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

    private void validarSaldo(String saldo, List<String> campos) {
        if (StringUtil.isNullOrEmpty(saldo)) {
            campos.add(MessageUtils.getString("cliente.saldo.obrigatorio"));
        } else {
            try {
                new BigDecimal(saldo);
            } catch (NumberFormatException e) {
                campos.add(MessageUtils.getString("cliente.saldo.invalido"));
            }
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
        } else if (ispbParticipante != ISPBParticipanteEnum.CX & ispbParticipante != ISPBParticipanteEnum.IF) {
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

    private void validarSituacaoConta(SituacaoContaEnum situacaoConta, List<String> campos) {
        if (situacaoConta == null) {
            campos.add(MessageUtils.getString("cliente.situacaoconta.obrigatorio"));
        } 
    }

    private void validarLimites(BigDecimal limitePixDiurno, BigDecimal limitePixNoturno, BigDecimal limitePixRedeSegura, List<String> campos) {
        if (limitePixDiurno == null) {
            campos.add(MessageUtils.getString("cliente.limitepixdiurno.obrigatorio"));
        } else if (limitePixDiurno.compareTo(BigDecimal.ZERO) < 0) {
            campos.add(MessageUtils.getString("cliente.limitepixdiurno.invalido"));
        }

        if (limitePixNoturno == null) {
            campos.add(MessageUtils.getString("cliente.limitepixnoturno.obrigatorio"));
        } else if (limitePixNoturno.compareTo(BigDecimal.ZERO) < 0) {
            campos.add(MessageUtils.getString("cliente.limitepixnoturno.invalido"));
        }

        if (limitePixRedeSegura == null) {
            campos.add(MessageUtils.getString("cliente.limitepixredesegura.obrigatorio"));
        } else if (limitePixRedeSegura.compareTo(BigDecimal.ZERO) < 0) {
            campos.add(MessageUtils.getString("cliente.limitepixredesegura.invalido"));
        } else if (limitePixRedeSegura.compareTo(limitePixDiurno) < 0) {
            campos.add(MessageUtils.getString("cliente.limitepixredesegura.invalido"));
        } else if (limitePixRedeSegura.compareTo(limitePixNoturno) < 0) {
            campos.add(MessageUtils.getString("cliente.limitepixredesegura.invalido"));
        }
    }
}
