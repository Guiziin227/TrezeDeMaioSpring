package com.guris.trezemaio.model;

import com.guris.trezemaio.model.enums.TipoItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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

    private String localization;
    private String description;

    @Enumerated(EnumType.STRING)
    private TipoItem type;

}
