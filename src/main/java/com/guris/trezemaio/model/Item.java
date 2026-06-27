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

    private String titulo;
    private String subtitulo;
    private int totalPaginas;
    private LocalDate dataPublicacao;
    private String idioma;
    private int quantidade;
    private String observacao;
    private Boolean ativo;

    private String autor;
    private String edicao;

    private String doador;

    @ManyToOne
    @JoinColumn(name = "editora_id")
    private Editora editora;

    private String localizacao;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoItem tipo;

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
        if (this.tipo == TipoItem.LIVRO) {
            return "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=300";
        } else if (this.tipo == TipoItem.JORNAL) {
            return "https://images.unsplash.com/photo-1504711434969-e33886168f5c?auto=format&fit=crop&q=80&w=300";
        } else if (this.tipo == TipoItem.REVISTA) {
            return "https://images.unsplash.com/photo-1506880018603-83d5b814b5a6?auto=format&fit=crop&q=80&w=300";
        }
        return "";
    }

    @Column(name = "image_url")
    private String imagemUrl;

    public String getCodigo() {
        return null;
    }

    public String getInfoEspecifica() {
        return "";
    }
}
