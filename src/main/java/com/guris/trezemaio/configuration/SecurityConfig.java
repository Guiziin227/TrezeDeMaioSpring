package com.guris.trezemaio.configuration;

import com.guris.trezemaio.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationProvider authenticationProvider) {
        httpSecurity
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/", "/index.html", "/index", "/login", "/css/**", "/img/**", "/js/**", "/usuario/login", "/acervo", "/error").permitAll()
                        .requestMatchers("/usuario/form", "/usuario/create").hasAuthority("ADMINISTRADOR")
                        .requestMatchers("/acervo/gerenciar", "/acervo/form", "/acervo/create", "/editora", "/editora/**").hasAnyAuthority("ADMINISTRADOR", "BIBLIOTECARIO")
                        //.requestMatchers("/dashboard").hasAnyAuthority("ADMINISTRADOR", "BIBLIOTECARIO")
                        .requestMatchers("/usuario/list").authenticated()
                        .requestMatchers("/admin").hasAuthority("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )
                .formLogin(f -> f
                        .loginPage("/usuario/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/usuario/login?error")
                        .permitAll()
                )
                .logout(l -> l.logoutSuccessUrl("/").permitAll());

        return httpSecurity.getOrBuild();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService,
                                                          PasswordEncoder encoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return authProvider;
    }
}
