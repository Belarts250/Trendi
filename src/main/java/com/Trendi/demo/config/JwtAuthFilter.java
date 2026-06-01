package com.Trendi.demo.config;


import com.Trendi.demo.service.UserDetailsServiceImpl;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    // OncePerRequestFilter ensures this filter runs ONCE per HTTP request

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Step 1: Get the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        // Step 2: Check if it's a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token? Continue the filter chain (request may be for a public endpoint)
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // Step 4: Extract email from token
        String email = jwtUtil.getEmailFromToken(token);

        // Step 5: If email exists and user is not already authenticated...
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from the database
            var userDetails = userDetailsService.loadUserByUsername(email);

            // Validate the token
            if (jwtUtil.validateToken(token)) {
                // Create an authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 6: Continue the filter chain
        filterChain.doFilter(request, response);
    }
}