package com.guris.trezemaio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Revista extends Item {


    @Column(nullable = true)
    private String issn;

    @Column(nullable = false)
    private String codigo;
}
