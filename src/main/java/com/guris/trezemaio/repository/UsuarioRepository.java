package com.guris.trezemaio.repository;

import com.guris.trezemaio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    @Query("select u from Usuario u where u.name = :name")
    Optional<Usuario> findByName(@Param("name") String name);

    boolean existsByName(String name);
}
