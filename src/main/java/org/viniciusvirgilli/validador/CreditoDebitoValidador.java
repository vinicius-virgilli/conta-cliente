package org.viniciusvirgilli.validador;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.viniciusvirgilli.dto.CadastroClienteDto;
import org.viniciusvirgilli.enums.ISPBParticipanteEnum;
import org.viniciusvirgilli.enums.TipoContaEnum;
import org.viniciusvirgilli.exception.ValidadorException;
import org.viniciusvirgilli.exception.dto.CamposComProblemasDto;
import org.viniciusvirgilli.util.MessageUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class CreditoDebitoValidador {

    public void validar(String cpfCnpj, TipoContaEnum tipoConta, BigDecimal valorCreditoDebito) {


        List<String> campos = new ArrayList<>();

        validarCpfCnpj(cpfCnpj, campos);
        validarValorCreditoDebido(valorCreditoDebito, campos);
        validarTipoConta(tipoConta, campos);

        if (!campos.isEmpty()) {
            throw new ValidadorException(new CamposComProblemasDto(campos));
        }
    }

    private void validarCpfCnpj(String cpfCnpj, List<String> campos) {
        if (cpfCnpj == null || cpfCnpj.isBlank()) {
            campos.add(MessageUtils.getString("cliente.cpfcnpj.obrigatorio"));
        } else if (!cpfCnpj.matches("\\d{11}|\\d{14}")) {
            campos.add(MessageUtils.getString("cliente.cpfcnpj.invalido"));
        }
    }

    private void validarValorCreditoDebido(BigDecimal saldo, List<String> campos) {
        if (saldo == null) {
            campos.add(MessageUtils.getString("creditoDebito.valor.obrigatorio"));
        } else if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
            campos.add(MessageUtils.getString("creditoDebito.valor.invalido"));
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
