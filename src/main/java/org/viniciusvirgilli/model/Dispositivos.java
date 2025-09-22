package org.viniciusvirgilli.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "dispositivos")
public class Dispositivos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "tipo")
    private Integer tipo;

    @NotNull
    @Column(name = "nome")
    private String nome;

    @NotNull
    @Column(name = "is_ativo")
    private String isAtivo;

    @NotNull
    @Column(name = "data_ativacao")
    private LocalDateTime dataAtivacao;

    @Column(name = "data_desativacao")
    private LocalDateTime dataDesativacao;
}
