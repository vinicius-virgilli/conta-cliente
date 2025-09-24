package org.viniciusvirgilli.dto;

import lombok.Data;
import org.viniciusvirgilli.enums.TipoContaEnum;

import java.math.BigDecimal;

@Data
public class DebitoDto {
    private String cpfCnpj;
    private TipoContaEnum tipoConta;
    private BigDecimal valorDebito;
}
