package com.guris.trezemaio.configuration; // Garanta que o pacote está correto

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Pega o mesmo caminho do diretório do usuário usado no seu controller
        String diretorioUsuario = System.getProperty("user.home");

        // No Spring, mapeamentos físicos precisam começar com o prefixo "file:"
        // O "file:" indica ao framework que ele deve buscar no disco rígido do Linux e não dentro do .jar
        String caminhoFisico = "file:" + diretorioUsuario + "/projeto_uploads/";

        // Configura o mapeamento URL -> Pasta Física
        registry.addResourceHandler("/img/acervo/**")
                .addResourceLocations(caminhoFisico);
    }
}
