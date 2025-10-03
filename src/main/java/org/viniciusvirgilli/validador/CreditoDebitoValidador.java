package org.viniciusvirgilli.validador;

import io.netty.util.internal.StringUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import org.viniciusvirgilli.dto.CreditoDebitoDto;
import org.viniciusvirgilli.enums.TipoContaEnum;
import org.viniciusvirgilli.enums.TipoOperacaoEnum;
import org.viniciusvirgilli.exception.ValidadorException;
import org.viniciusvirgilli.exception.dto.CamposComProblemasDto;
import org.viniciusvirgilli.util.DataUtil;
import org.viniciusvirgilli.util.MessageUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Slf4j
public class CreditoDebitoValidador {

    public void validar(CreditoDebitoDto dto) {


        List<String> campos = new ArrayList<>();

        validarCpfCnpj(dto.getCpfCnpj(), campos);
        validarValor(dto.getValor(), campos);
        validarTipoConta(dto.getTipoConta(), campos);
        validarDataOperacao(dto.getDataOperacao(), campos);
        validarTipoOperacao(dto.getTipoOperacao(), campos);

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

    private void validarValor(String valor, List<String> campos) {
        if (StringUtil.isNullOrEmpty(valor)) {
            campos.add(MessageUtils.getString("creditoDebito.valor.obrigatorio"));
        } else {
            try {
                BigDecimal valorBigDecimal = new BigDecimal(valor);
                if (valorBigDecimal.compareTo(BigDecimal.ZERO) <= 0) {
                    campos.add(MessageUtils.getString("creditoDebito.valor.invalido"));
                }
            } catch (NumberFormatException e) {
                campos.add(MessageUtils.getString("creditoDebito.valor.invalido"));
            }
        }
    }

    private void validarTipoConta(TipoContaEnum tipoConta, List<String> campos) {
        if (tipoConta == null) {
            campos.add(MessageUtils.getString("cliente.tipoconta.obrigatorio"));
        } else if (!(tipoConta == TipoContaEnum.CACC || tipoConta == TipoContaEnum.SVGS)) {
            campos.add(MessageUtils.getString("cliente.tipoconta.invalido"));
        }
    }

    private void validarDataOperacao(String dataOperacao, List<String> campos) {
        if (dataOperacao == null || dataOperacao.isBlank()) {
            campos.add(MessageUtils.getString("creditoDebito.dataoperacao.obrigatorio"));
        } else if (!DataUtil.isDataOperacaoValida(dataOperacao)) {
            campos.add(MessageUtils.getString("creditoDebito.dataoperacao.invalida"));
        }
    }

    private void validarTipoOperacao(TipoOperacaoEnum tipoOperacao, List<String> campos) {
        if (tipoOperacao == null) {
            campos.add(MessageUtils.getString("creditoDebito.tipooperacao.obrigatorio"));
        } else if (!(tipoOperacao == TipoOperacaoEnum.CREDITO || tipoOperacao == TipoOperacaoEnum.DEBITO)) {
            campos.add(MessageUtils.getString("creditoDebito.tipooperacao.invalido"));
        }
    }
}
