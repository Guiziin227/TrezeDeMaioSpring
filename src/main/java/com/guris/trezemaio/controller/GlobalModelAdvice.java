package com.guris.trezemaio.controller;

import com.guris.trezemaio.service.AcessoService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final AcessoService acessoService;

    public GlobalModelAdvice(AcessoService acessoService) {
        this.acessoService = acessoService;
    }

    @ModelAttribute("acessos")
    public long getAcessos() {
        return acessoService.obterTotalAcessos();
    }
}
