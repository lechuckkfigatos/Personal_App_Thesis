package org.lechuck.personal_app.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.lechuck.personal_app.Entity.MyUserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.lechuck.personal_app.Service.JWTService;
import org.lechuck.personal_app.Service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    ApplicationContext context;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        Integer userId = null;

        logger.debug("Authorization header: {}", authHeader);

        // Extract token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            logger.debug("Extracted token: {}", token);
            try {
                username = jwtService.extractUsername(token);
                userId = jwtService.extractUserId(token);
                logger.debug("Extracted username: {}, userId: {}", username, userId);
            } catch (Exception e) {
                logger.error("Failed to extract token details: {}", e.getMessage());
            }
        } else {
            logger.warn("No valid Authorization header found");
        }

        // Authenticate if no existing authentication
        if (username != null && userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.context.getBean(MyUserDetailService.class).loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {
                    org.lechuck.personal_app.Config.CustomUserDetails customUserDetails = new org.lechuck.personal_app.Config.CustomUserDetails((MyUserDetail) userDetails, userId);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(customUserDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication set for username: {}", username);
                } else {
                    logger.warn("Invalid or expired token for username: {}", username);
                }
            } catch (Exception e) {
                logger.error("Authentication failed for username: {}: {}", username, e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed: " + e.getMessage());
                return;
            }
        } else if (username == null || userId == null) {
            logger.warn("Skipping authentication: username or userId is null");
        }

//        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            UserDetails userDetails = this.context.getBean(MyUserDetailService.class).loadUserByUsername(userName);
//            if (jwtService.validateToken(token, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken =
//                        new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            }
        filterChain.doFilter(request, response);
    }
}
