package com.guris.trezemaio.repository;

import com.guris.trezemaio.model.Item;
import com.guris.trezemaio.model.enums.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findFirstByTypeOrderByIdDesc(TipoItem type);
}
