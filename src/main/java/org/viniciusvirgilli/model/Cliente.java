package org.viniciusvirgilli.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.viniciusvirgilli.enums.TipoContaEnum;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "simulacao_realizada")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta")
    private Long idConta;

    @NotNull
    @Column(name = "nome")
    private String nome;

    @NotNull
    @Column(name = "cpf_cnpj")
    private String cpjCnpj;

    @NotNull
    @Column(name = "saldo")
    private Integer saldo;

    @NotNull
    @Column(name = "agencia")
    private String agencia;

    @NotNull
    @Column(name = "conta")
    private TipoContaEnum conta;

    @NotNull
    @Column(name = "tipo_conta")
    private TipoContaEnum tipoConta;

    @NotNull
    @Column(name = "ispbParticipante")
    private String ispbParticipante;

}
