package org.example.ecommerce.Tokens;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.ecommerce.GlobalExceptions.InvalidJwtToken;
import org.example.ecommerce.Service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtForgot {
    private static final int EXPIRATION_TIME = 15*1000*60;
    private static final String JWT_SECRET_KEY = "dXNlcmN1c3RvbWVycmVnaXNUUkF0aW9u";


    private static ForgotPasswordService forgotPasswordService;
    @Autowired
    public JwtForgot(ForgotPasswordService service){
        JwtForgot.forgotPasswordService=service;
    }

    public static String generateForgetPasswordToken(String email){

        return JWT.create()
                .withSubject(email)
                .withClaim("use","password-reset")
                .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(JWT_SECRET_KEY));
    }

    public static String validateForgetPasswordToken(String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET_KEY))
                    .acceptExpiresAt(0)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("use").asString();
        }
        catch (TokenExpiredException e) {
            forgotPasswordService.removeToken(token);
            throw new InvalidJwtToken("Token has expired. Please request a new one.");
        } catch (JWTVerificationException e) {
            throw new InvalidJwtToken("Invalid token. Please request a new one.");
        }
    }

    public static String returnEmailFromToken(String token){
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }
    }
