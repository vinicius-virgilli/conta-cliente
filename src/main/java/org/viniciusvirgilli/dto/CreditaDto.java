package org.viniciusvirgilli.dto;

import lombok.Data;
import lombok.Getter;
import org.viniciusvirgilli.enums.TipoContaEnum;

import java.math.BigDecimal;

@Data
public class CreditaDto {
    private String cpfCnpj;
    private TipoContaEnum tipoConta;
    private BigDecimal valorCredito;
}
