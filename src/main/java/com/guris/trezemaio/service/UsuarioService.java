package com.guris.trezemaio.service;

import com.guris.trezemaio.model.Usuario;
import com.guris.trezemaio.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void criarUsuario(Usuario usuario){
        logger.info("Criando usuário: {}", usuario.getNome());

        if (usuarioRepository.existsByNome(usuario.getNome())) {
            throw new IllegalArgumentException("Já existe um usuário com este nome.");
        }

        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + id));
    }

    @Transactional
    public void deletarUsuario(UUID id) {
        logger.info("Deletando usuário com ID: {}", id);
        usuarioRepository.deleteById(id);
    }
}
