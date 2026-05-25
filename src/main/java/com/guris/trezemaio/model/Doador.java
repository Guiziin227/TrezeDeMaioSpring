package com.guris.trezemaio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_doador")
public class Doador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
}
