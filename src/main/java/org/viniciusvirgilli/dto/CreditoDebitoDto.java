package org.viniciusvirgilli.dto;

import lombok.Data;
import org.viniciusvirgilli.enums.TipoContaEnum;
import org.viniciusvirgilli.enums.TipoOperacaoEnum;

import java.math.BigDecimal;

@Data
public class CreditoDebitoDto {
    private TipoOperacaoEnum tipoOperacao;
    private String dataOperacao;
    private String cpfCnpj;
    private TipoContaEnum tipoConta;
    private String valor;
}
