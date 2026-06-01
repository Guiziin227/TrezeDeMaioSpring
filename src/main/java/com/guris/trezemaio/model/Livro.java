package com.guris.trezemaio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
public class Livro extends Item {

    private String isbn;
    private String assuntos;

    @Column(nullable = false)
    private String codigo;
}
