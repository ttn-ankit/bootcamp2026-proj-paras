package org.example.ecommerce.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ecommerce.Service.AccessTokenService;
import org.example.ecommerce.Service.CustomUserDetailsService;
import org.example.ecommerce.Tokens.JwtLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CustomJWTAuthenticationAuthorization extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Autowired
    private AccessTokenService tokenService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.substring(7).isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            String use = JwtLogin.validateTokenClaim(token);
            if (!use.equals("access")) {
                throw new CustomAuthenticationException("This is not an access token can not be used for authentication");

            }
            if (!tokenService.findToken(token)) {
                throw new CustomAuthenticationException("this token is not valid");
            }

            String email = JwtLogin.validateLoginAccessTokenToProvideAnotherUsingJwtToken(token);

            if (email != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                System.out.println(authToken);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                filterChain.doFilter(request, response);
            }
        } catch (CustomAuthenticationException ex) {
            throw ex;
        }
    }
}
