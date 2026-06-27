package com.guris.trezemaio.repository;

import com.guris.trezemaio.model.Item;
import com.guris.trezemaio.model.enums.TipoItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findFirstByTipoOrderByIdDesc(TipoItem tipo);

    boolean existsByTitulo(String titulo);

    @Query("SELECT i FROM Item i WHERE " +
            "(:tipo IS NULL OR i.tipo = :tipo) AND " +
            "(:isAdminOrBibliotecario = true OR i.ativo = true) AND " +
            "(:query IS NULL OR LOWER(i.titulo) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(i.autor) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(i.descricao) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Item> searchAcervo(
            @Param("tipo") TipoItem tipo,
            @Param("query") String query,
            @Param("isAdminOrBibliotecario") boolean isAdminOrBibliotecario,
            Pageable pageable
    );


    @Query("SELECT i FROM Item i WHERE " +
            "(:query IS NULL OR LOWER(i.titulo) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(i.autor) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(i.codigo) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Item> searchGerenciar(
            @Param("query") String query,
            Pageable pageable
    );
}

