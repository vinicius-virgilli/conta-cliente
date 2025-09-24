package org.viniciusvirgilli.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.viniciusvirgilli.enums.TipoContaEnum;
import org.viniciusvirgilli.enums.ISPBParticipanteEnum;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "cliente",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"cpf_cnpj", "tipo_conta"})}
)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nome")
    private String nome;

    @NotNull
    @Column(name = "cpf_cnpj")
    private String cpfCnpj;

    @NotNull
    @Column(name = "saldo")
    private BigDecimal saldo;

    @NotNull
    @Column(name = "agencia")
    private String agencia;

    @NotNull
    @Column(name = "conta")
    private Integer conta;

    @NotNull
    @Column(name = "operacao")
    private Integer operacao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta")
    private TipoContaEnum tipoConta;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ispb_participante")
    private ISPBParticipanteEnum ispbParticipante;

}
