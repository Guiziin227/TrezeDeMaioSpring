package com.guris.trezemaio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Jornal extends Item{

    private String secao;

    private String cidade;

    @Column(nullable = false)
    private String codigo;
}
