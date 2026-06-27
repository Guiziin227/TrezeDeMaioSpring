package com.guris.trezemaio.repository;

import com.guris.trezemaio.model.Editora;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EditoraRepository extends JpaRepository<Editora, Long> {

    @Query("SELECT e FROM Editora e WHERE " +
            "(:query IS NULL OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(e.cnpj) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Editora> searchEditoras(@Param("query") String query);
}
