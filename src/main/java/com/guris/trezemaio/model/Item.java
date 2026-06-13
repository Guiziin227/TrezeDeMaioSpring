package com.guris.trezemaio.model;

import com.guris.trezemaio.model.enums.TipoItem;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "tb_item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subtitle;
    private int pagesCount;
    private LocalDate publicationDate;
    private String language;
    private int quantity;
    private String observation;
    private Boolean isActive;

    private String autor;
    private String edicao;

    @ManyToOne
    @JoinColumn(name = "doador_id")
    private Doador doador;

    @ManyToOne
    @JoinColumn(name = "editora_id")
    private Editora editora;

    private String localization;
    private String description;

    @Enumerated(EnumType.STRING)
    private TipoItem type;

    public String getCodigo() {
        return null;
    }

    public String getInfoEspecifica() {
        return "";
    }
}
