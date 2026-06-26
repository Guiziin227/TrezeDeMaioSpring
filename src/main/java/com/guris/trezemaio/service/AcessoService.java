package com.guris.trezemaio.service;

import com.guris.trezemaio.model.Acesso;
import com.guris.trezemaio.repository.AcessoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class AcessoService {

    private static final Logger logger = LoggerFactory.getLogger(AcessoService.class);
    private final AcessoRepository acessoRepository;

    public AcessoService(AcessoRepository acessoRepository) {
        this.acessoRepository = acessoRepository;
    }

    @Transactional
    public void registrarAcesso(String ip, String userAgent, String uri) {
        logger.info("Registrando acesso. IP: {}, URI: {}", ip, uri);
        try {
            Acesso acesso = new Acesso(LocalDateTime.now(), ip, userAgent, uri);
            acessoRepository.save(acesso);
        } catch (Exception e) {
            logger.error("Erro ao salvar registro de acesso: ", e);
        }
    }

    @Transactional(readOnly = true)
    public long obterTotalAcessos() {
        return acessoRepository.count();
    }
}
