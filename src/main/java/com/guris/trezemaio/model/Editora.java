package com.guris.trezemaio.model;

import jakarta.persistence.*;

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

    public Endereco getAddress() {
        return address;
    }

    public void setAddress(Endereco address) {
        this.address = address;
    }

}
