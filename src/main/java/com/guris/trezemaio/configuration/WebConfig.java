package com.guris.trezemaio.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String caminhoProjeto = new File("").getAbsolutePath();

        String caminhoFisico = "file:" + caminhoProjeto + "/src/main/resources/static/uploads/";

        registry.addResourceHandler("/img/acervo/**")
                .addResourceLocations(caminhoFisico);
    }
}