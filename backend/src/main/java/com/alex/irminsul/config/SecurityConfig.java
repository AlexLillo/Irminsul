package com.alex.irminsul.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/games/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/games").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/games/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/games/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/platforms/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/platforms").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/platforms/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/platforms/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}