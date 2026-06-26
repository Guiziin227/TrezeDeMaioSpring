package com.guris.trezemaio.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AcessoInterceptor acessoInterceptor;

    public WebConfig(AcessoInterceptor acessoInterceptor) {
        this.acessoInterceptor = acessoInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String caminhoProjeto = new File("").getAbsolutePath();

        String caminhoFisico = "file:" + caminhoProjeto + "/src/main/resources/static/uploads/";

        registry.addResourceHandler("/img/acervo/**")
                .addResourceLocations(caminhoFisico);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(acessoInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/js/**", "/img/**", "/webjars/**", "/error", "/favicon.ico");
    }
}