package com.guris.trezemaio.model;

import com.guris.trezemaio.model.enums.TipoUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String password;

    @Enumerated(EnumType.STRING)
    private TipoUsuario type;
}
