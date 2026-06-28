package com.guris.trezemaio.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.guris.trezemaio.model.Usuario;
import com.guris.trezemaio.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void salvarUsuario(Usuario usuario) {
        logger.info("Salvando usuário: {}", usuario.getNome());

        usuarioRepository.findByNome(usuario.getNome()).ifPresent(existente -> {
            if (usuario.getId() == null || !existente.getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Já existe um usuário com este nome.");
            }
        });

        if (usuario.getId() == null) {
            if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
                throw new IllegalArgumentException("A senha é obrigatória para um novo usuário.");
            }
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        } else {
            Usuario usuarioNoBanco = usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuario.getId()));

            if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
                usuario.setSenha(usuarioNoBanco.getSenha());
            } else {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
            }
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
