package com.guris.trezemaio.dto;

import com.guris.trezemaio.model.enums.TipoItem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ItemDTO {
    // Adicione esta linha para suportar a edição:
    private Long id;

    private TipoItem type;

    private String title;
    private String subtitle;
    private int pagesCount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    private String language;
    private int quantity;
    private String observation;
    private String autor;
    private String edicao;
    private String localization;
    private String description;

    private Long doadorId;
    private Long editoraId;


    private String isbn;       // Livro
    private String assuntos;   // Livro
    private String secao;      // Jornal
    private String cidade;     // Jornal
    private String issn;       // Revista
    private String codigo;     // Livro / Jornal / Revista

    private String imagemUrl;

    private boolean isActive = true;
}
