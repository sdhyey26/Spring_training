package com.tss.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

                // ✅ Auth endpoints (public)
                .requestMatchers("/api/auth/**").permitAll()

                // 🔒 Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("Admin")

                // 🔒 Account management
                .requestMatchers("/api/accounts/my-account/**").hasRole("Customer")
                .requestMatchers("/api/accounts/**").hasRole("Admin")  

                // 🔒 Cards
                .requestMatchers(HttpMethod.POST, "/api/cards/apply").hasRole("Customer")
                .requestMatchers(HttpMethod.GET, "/api/cards/**").hasRole("Admin")

                // 🔒 Transactions
                .requestMatchers(HttpMethod.POST, "/api/transactions/transfer").hasRole("Customer")
                .requestMatchers(HttpMethod.POST, "/api/transactions/**").hasAnyRole("Admin", "Customer")
                .requestMatchers(HttpMethod.GET, "/api/transactions/**").hasAnyRole("Admin", "Customer")

                // 🔒 Loans
                .requestMatchers("/api/loans/my-loans/**").hasRole("Customer")
                .requestMatchers(HttpMethod.POST, "/api/loans/apply").hasRole("Customer")
                .requestMatchers(HttpMethod.POST, "/api/loans/payment").hasRole("Customer")
                .requestMatchers(HttpMethod.GET, "/api/loans/*/payments").hasRole("Customer")
                .requestMatchers(HttpMethod.GET, "/api/loans/*").hasAnyRole("Admin", "Customer")
                .requestMatchers(HttpMethod.GET, "/api/loans").hasRole("Admin")
                .requestMatchers(HttpMethod.GET, "/api/loans/status/**").hasRole("Admin")
                .requestMatchers(HttpMethod.POST, "/api/loans/approve").hasRole("Admin")
                .requestMatchers(HttpMethod.POST, "/api/loans/*/disburse").hasRole("Admin")

                // 🔒 Everything else
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
