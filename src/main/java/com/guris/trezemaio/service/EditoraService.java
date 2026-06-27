package com.guris.trezemaio.service;

import com.guris.trezemaio.model.Editora;
import com.guris.trezemaio.repository.EditoraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EditoraService {

    private static final Logger logger = LoggerFactory.getLogger(EditoraService.class);

    private final EditoraRepository editoraRepository;

    public EditoraService(EditoraRepository editoraRepository) {
        this.editoraRepository = editoraRepository;
    }

    @Transactional(readOnly = true)
    public List<Editora> listarTodas() {
        logger.info("Listando todas as editoras.");
        return editoraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Editora> buscarComFiltro(String query) {
        logger.info("Buscando editoras com query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            return editoraRepository.findAll();
        }
        return editoraRepository.searchEditoras(query.trim());
    }

    @Transactional(readOnly = true)
    public Editora buscarPorId(Long id) {
        logger.info("Buscando editora por ID: {}", id);
        return editoraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Editora não encontrada com o ID: " + id));
    }

    @Transactional
    public Editora salvar(Editora editora) {
        logger.info("Salvando editora: {}", editora.getNome());
        return editoraRepository.save(editora);
    }

    @Transactional
    public void excluirPorId(Long id) {
        logger.info("Excluindo editora por ID: {}", id);
        if (!editoraRepository.existsById(id)) {
            throw new IllegalArgumentException("Editora não encontrada com o ID: " + id);
        }
        editoraRepository.deleteById(id);
    }
}
