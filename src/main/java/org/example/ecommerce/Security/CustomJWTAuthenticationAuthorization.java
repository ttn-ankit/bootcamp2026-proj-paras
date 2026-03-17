package org.example.ecommerce.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ecommerce.GlobalExceptions.APIException;
import org.example.ecommerce.Service.AccessTokenService;
import org.example.ecommerce.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class    CustomJWTAuthenticationAuthorization extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private AccessTokenService tokenService;

    @Autowired
    private JWTService jwtService;
    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try{
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


            String token = authHeader.substring(7);

            if (token.isBlank()) {
                throw new APIException("Invalid Auth!", HttpStatus.UNAUTHORIZED);
            }


            if (!tokenService.findToken(token)) {
                throw new APIException("Invalid Token", HttpStatus.UNAUTHORIZED);
            }

            String email = jwtService.extractUsername(token);

            if(!jwtService.validateToken(token,email)){
                throw new APIException("Invalid Token", HttpStatus.UNAUTHORIZED);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);


                if(jwtService.validateToken(token,email)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    filterChain.doFilter(request, response);
                }
                else {
                    throw new APIException("Invalid Token", HttpStatus.UNAUTHORIZED);
                }
                }
            }
        catch (Exception ex){
            handlerExceptionResolver.resolveException(request,response,null,ex);
        }
    }
}