package com.guris.trezemaio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_editora")
public class Editora {

    @Id
    private Long id;

    private String name;

    private String cnpj;

    @OneToOne
    @JoinColumn(name = "address_id")
    private Endereco address;
}
