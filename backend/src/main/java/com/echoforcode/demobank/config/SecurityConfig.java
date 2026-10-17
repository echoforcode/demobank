package com.echoforcode.demobank.config;

import com.echoforcode.demobank.filter.JwtAuthenticationFilter;
import com.echoforcode.demobank.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

/*
register the filter,configure stateless requests,reserve the future login/register routers
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationProvider provider, JwtService jwt,
                                                   UserDetailsService users) throws Exception {
        var filter = new JwtAuthenticationFilter(jwt, users);
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST,
                                "/user/auth", "/user/register").permitAll() //permit
                        .anyRequest().authenticated())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((request, response, ex) ->
                                response.setStatus(401)))
                .authenticationProvider(provider)
                .addFilterBefore(filter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("http://localhost:5173"));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH",
                "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}