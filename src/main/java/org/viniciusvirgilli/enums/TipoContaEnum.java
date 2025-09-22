package org.viniciusvirgilli.enums;

public enum TipoContaEnum {
    CACC(1, 1340, "Conta Corrente"),
    SVGS(2, 3701, "conta de poupanca");

    TipoContaEnum(Integer idEnum, Integer operacao, String descricao) {}
}
