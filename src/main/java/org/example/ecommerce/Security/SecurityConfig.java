package org.example.ecommerce.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static java.rmi.server.LogStream.log;

@Slf4j
@Configuration
public class SecurityConfig {
    @Autowired
    private CustomAuthenticationProvider authenticationProvider;
    @Autowired
    private CustomJWTAuthenticationAuthorization jwtAuthenticationAuthorization;
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    private CustomAuthEntryPoint customAuthEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("Configuring security filter chain");

        http.csrf(csrf->csrf.disable());
        http.authorizeHttpRequests(auth
                        -> auth
                        .requestMatchers("/api/user/login",
                                "/api/user/register-customer",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationAuthorization, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(customAuthEntryPoint)
                .accessDeniedHandler((customAccessDeniedHandler)));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(authenticationProvider));
    }
}

