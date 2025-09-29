package com.tss.Config;

import java.io.IOException;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (tokenProvider.validateToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            	var authorities = tokenProvider.getRoles(token).stream()
            		    .map(role -> role.replaceFirst("^ROLE_", "")) 
            		    .map(role -> "ROLE_" + role)                  
            		    .map(SimpleGrantedAuthority::new)
            		    .collect(Collectors.toList());
                var auth = new UsernamePasswordAuthenticationToken(tokenProvider.getUsername(token), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("Authorities from JWT: " + authorities);

            }
        }

        filterChain.doFilter(request, response);
    }


}


