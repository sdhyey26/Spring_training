package com.tss.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/auth/forgot-password", "/api/auth/reset-password").permitAll()
        
            .requestMatchers("/api/admin/**").hasRole("Admin")
        
            .requestMatchers(HttpMethod.GET, "/api/accounts/**").hasAnyRole("Admin", "Customer")
            .requestMatchers(HttpMethod.POST, "/api/accounts/**").hasRole("Customer")
            .requestMatchers(HttpMethod.PUT, "/api/accounts/**").hasRole("Customer")
            .requestMatchers(HttpMethod.DELETE, "/api/accounts/**").hasRole("Customer")
        
            .requestMatchers("/api/cards/**").hasRole("Customer")
        
            .requestMatchers(HttpMethod.GET, "/api/transactions/**").hasAnyRole("Admin", "Customer")
            .requestMatchers(HttpMethod.POST, "/api/transactions/**").hasRole("Customer")
        
            .anyRequest().authenticated()
        )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(b -> b.disable())
            .formLogin(form -> form.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
