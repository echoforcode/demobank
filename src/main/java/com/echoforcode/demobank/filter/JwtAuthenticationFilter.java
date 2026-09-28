package com.echoforcode.demobank.filter;


import com.echoforcode.demobank.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
// Constructed only by SecurityConfig: do not add @Component here.

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UserDetailsService users;
    public JwtAuthenticationFilter(JwtService jwt,
                                   UserDetailsService users) {
        this.jwt = jwt;
        this.users = users;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.regionMatches(
                true, 0, "Bearer ", 0, 7)) {
            chain.doFilter(request, response);
            return;
        }
        if (SecurityContextHolder.getContext()
                .getAuthentication() == null) {
            try {
                String token = header.substring(7).trim();
                String subject = jwt.extractSubject(token);
                var user = users.loadUserByUsername(subject);
                if (!user.isEnabled() || !user.isAccountNonLocked() ||
                        !user.isAccountNonExpired() ||
                        !user.isCredentialsNonExpired()) {
                    response.setStatus(401);
                    return;
                }
                var auth = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
            } catch (JwtException | IllegalArgumentException |
                     UsernameNotFoundException ex) {
                SecurityContextHolder.clearContext();
                response.setStatus(401);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}

