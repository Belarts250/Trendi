package com.Trendi.demo.config;

import com.Trendi.demo.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Allow preflight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Allow public endpoints without token validation
        String requestUri = request.getRequestURI();
        if (isPublicEndpoint(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // No token -> continue (SecurityConfig will reject if required)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Extract and validate token
        String token = authHeader.substring(7).trim();
        String email = jwtUtil.getEmailFromToken(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Continue filter chain
        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/auth/") ||
                uri.startsWith("/uploads/") ||
                uri.startsWith("/api/files/") ||
                uri.equals("/api/contact") ||
                uri.startsWith("/api/articles") ||
                uri.startsWith("/articles"); // if you have direct /articles endpoints
    }
}