package org.viniciusvirgilli.dto;

import lombok.Data;
import org.viniciusvirgilli.enums.ISPBParticipanteEnum;
import org.viniciusvirgilli.enums.SituacaoContaEnum;
import org.viniciusvirgilli.enums.TipoContaEnum;

import java.math.BigDecimal;

@Data
public class CadastroClienteDto {
    private String nome;
    private String cpfCnpj;
    private String saldo;
    private String agencia;
    private Integer conta;
    private TipoContaEnum tipoConta;
    private Integer operacao;
    private ISPBParticipanteEnum ispbParticipante;
    private SituacaoContaEnum situacaoConta;
    private BigDecimal limitePixDiurno;
    private BigDecimal limitePixNoturno;
    private BigDecimal limitePixRedeSegura;
}
