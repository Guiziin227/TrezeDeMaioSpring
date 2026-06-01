package com.guris.trezemaio.repository;

import com.guris.trezemaio.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
