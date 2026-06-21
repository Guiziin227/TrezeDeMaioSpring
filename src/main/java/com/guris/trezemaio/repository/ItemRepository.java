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
    Optional<Item> findFirstByTypeOrderByIdDesc(TipoItem type);

    boolean existsByTitle(String title);

    @Query("SELECT i FROM Item i WHERE " +
            "(:type IS NULL OR i.type = :type) AND " +
            "(:isAdminOrBibliotecario = true OR i.isActive = true) AND " +
            "(:query IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(i.autor) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Item> searchAcervo(
            @Param("type") TipoItem type,
            @Param("query") String query,
            @Param("isAdminOrBibliotecario") boolean isAdminOrBibliotecario,
            Pageable pageable
    );
}

