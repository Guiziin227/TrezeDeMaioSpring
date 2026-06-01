package com.guris.trezemaio.service;

import com.guris.trezemaio.model.Livro;
import com.guris.trezemaio.repository.LivroRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivroService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @Transactional
    public void cadastrarLivro(Livro livro) {
        logger.info("Cadastrando livro");
        livroRepository.save(livro);
    }

    @Transactional(readOnly = true)
    public List<Livro> listarLivros() {
        logger.info("Listando livros");
        return livroRepository.findAll();
    }
}
