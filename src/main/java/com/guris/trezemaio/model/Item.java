package com.guris.trezemaio.model;

import com.guris.trezemaio.model.enums.TipoItem;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "tb_item")
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Column(name = "data_modificacao")
    private LocalDateTime dataModificacao;

    @CreatedBy
    @Column(name = "criado_por")
    private String criadoPor;

    @LastModifiedBy
    @Column(name = "modificado_por")
    private String modificadoPor;

    public String getImagemExibicao() {
        if (this.imagemUrl != null) {
            return "/img/acervo/" + this.imagemUrl;
        }
        if (this.type == com.guris.trezemaio.model.enums.TipoItem.LIVRO) {
            return "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=300";
        } else if (this.type == com.guris.trezemaio.model.enums.TipoItem.JORNAL) {
            return "https://images.unsplash.com/photo-1504711434969-e33886168f5c?auto=format&fit=crop&q=80&w=300";
        } else if (this.type == com.guris.trezemaio.model.enums.TipoItem.REVISTA) {
            return "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?auto=format&fit=crop&q=80&w=300";
        }
        return "";
    }

    // ALTERAÇÃO: Atributo movido para junto dos outros campos.
    // Como a tabela usa o padrão snake_case ("image_url" que você mencionou antes),
    // certifique-se de que o name corresponda exatamente ao que está no banco de dados.
    @Column(name = "image_url")
    private String imagemUrl;


    public String getCodigo() {
        return null;
    }

    public String getInfoEspecifica() {
        return "";
    }
}
