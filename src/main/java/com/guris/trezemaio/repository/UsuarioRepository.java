package com.guris.trezemaio.repository;

import com.guris.trezemaio.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    @Query("select u from Usuario u where u.nome = :nome")
    Optional<Usuario> findByNome(@Param("nome") String nome);

    boolean existsByNome(String nome);
}
