package com.guris.trezemaio.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Pega o mesmo caminho do diretório do usuário usado no controller
        String diretorioUsuario = System.getProperty("user.home");

        // No Spring, mapeamentos físicos precisam começar com o prefixo "file:"
        String caminhoFisico = "file:" + diretorioUsuario + "/projeto_uploads/";

        // Configura o mapeamento URL -> Pasta Física
        registry.addResourceHandler("/img/acervo/**")
                .addResourceLocations(caminhoFisico);
    }
}
