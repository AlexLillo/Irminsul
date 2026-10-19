
package com.alex.irminsul.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:4200"));
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(
                List.of("Content-Type", "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository)
            throws Exception {

        http
                .cors(Customizer.withDefaults())

                .csrf(csrf -> csrf
                        .csrfTokenRepository(
                                CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(
                                new CsrfTokenRequestAttributeHandler())
                )

                .securityContext(context -> context
                        .securityContextRepository(securityContextRepository)
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.GET, "/api/auth/csrf").permitAll()
                        .requestMatchers(
                                HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(
                                HttpMethod.POST, "/api/auth/logout").permitAll()

                        .requestMatchers(
                                HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(
                                HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers(
                                HttpMethod.GET, "/api/users").hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET, "/api/games/**").permitAll()
                        .requestMatchers(
                                HttpMethod.POST, "/api/games").hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/api/games/**").hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE, "/api/games/**").hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET, "/api/platforms/**").authenticated()
                        .requestMatchers(
                                HttpMethod.POST, "/api/platforms").hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.PUT, "/api/platforms/**").hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.DELETE, "/api/platforms/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
