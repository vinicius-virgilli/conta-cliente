package org.viniciusvirgilli.dto;

import lombok.Data;
import org.viniciusvirgilli.enums.ISPBParticipanteEnum;
import org.viniciusvirgilli.enums.TipoContaEnum;

import java.math.BigDecimal;

@Data
public class CadastroClienteDto {
    private String nome;
    private String cpfCnpj;
    private BigDecimal saldo;
    private String agencia;
    private Integer conta;
    private TipoContaEnum tipoConta;
    private Integer operacao;
    private ISPBParticipanteEnum ispbParticipante;
}
