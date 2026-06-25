package com.guris.trezemaio.dto;

import com.guris.trezemaio.model.enums.TipoItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ItemDTO {

    private Long id;

    @NotNull(message = "O tipo do item é obrigatório.")
    private TipoItem type;

    @NotBlank(message = "O título é obrigatório.")
    private String title;
    private String subtitle;
    private int pagesCount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publicationDate;

    private String language;

    @Min(value = 0, message = "A quantidade não pode ser negativa.")
    private int quantity;
    private String observation;

    @NotBlank(message = "O autor é obrigatório.")
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
