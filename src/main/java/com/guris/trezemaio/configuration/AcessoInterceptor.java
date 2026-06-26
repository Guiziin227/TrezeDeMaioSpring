package com.guris.trezemaio.configuration;

import com.guris.trezemaio.service.AcessoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AcessoInterceptor implements HandlerInterceptor {

    private final AcessoService acessoService;

    public AcessoInterceptor(AcessoService acessoService) {
        this.acessoService = acessoService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        jakarta.servlet.http.HttpSession session = request.getSession(true);
        if (session.getAttribute("acesso_registrado") == null) {
            String ip = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            String uri = request.getRequestURI();

            acessoService.registrarAcesso(ip, userAgent, uri);
            session.setAttribute("acesso_registrado", true);
        }
        return true;
    }
}
