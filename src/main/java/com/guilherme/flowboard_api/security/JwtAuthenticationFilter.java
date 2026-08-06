package com.guilherme.flowboard_api.security;

import com.guilherme.flowboard_api.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        log.debug("Processing request to: {} with authHeader: {}", request.getRequestURI(), authHeader != null ? "present" : "null");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found, proceeding to next filter");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);
            log.debug("Extracted email from token: {}", email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(email);
                log.debug("Loaded user details for email: {} - type: {}", email, userDetails.getClass().getName());
                
                if (userDetails instanceof UserDetailsImpl) {
                    UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;

                    if (jwtService.isTokenValid(token, userDetailsImpl)) {
                        log.debug("Token is valid for user: {}", email);
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetailsImpl, null, userDetailsImpl.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("Authentication set in SecurityContext for user: {}", email);
                    } else {
                        log.warn("Token is invalid for user: {}", email);
                    }
                } else {
                    log.error("UserDetails is not an instance of UserDetailsImpl: {}", userDetails.getClass().getName());
                }
            } else {
                log.debug("Email is null or authentication already exists");
            }
        } catch (Exception ex) {
            log.error("Error in JWT authentication: ", ex);
            SecurityContextHolder.clearContext();
        }

        log.debug("Proceeding to next filter");
        filterChain.doFilter(request, response);
    }
}
