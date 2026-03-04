package org.example.ecommerce.Tokens;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.ecommerce.GlobalExceptions.InvalidJwtToken;

import java.util.Date;

public class JwtLogin {

    private static final int EXPIRATION_TIME = 1000*60*60*3;
    private static final String JWT_SECRET_KEY = "dXNlcmN1c3RvbWVycmVnaXNUUkF0aW9u";
    private static final int REFRESH_TOKEN_EXPIRATION_TIME = 1000*60*60*24;

    public static String generateLoginAccessToken(String email){

        return JWT.create()
                .withClaim("use","access")
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(JWT_SECRET_KEY));
    }

    public static String validateLoginAccessToken(String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET_KEY))
                    .acceptExpiresAt(0)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (TokenExpiredException e) {
            throw new InvalidJwtToken("Token has expired. Please request a new one.");
        } catch (JWTVerificationException e) {
            throw new InvalidJwtToken("Invalid token. Please request a new one.");
        }
    }
    public static String generateLoginRefreshToken(String email) {
        return JWT.create()
                .withClaim("use","refresh")
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis()+REFRESH_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(JWT_SECRET_KEY));
    }


    public static String validateLoginAccessTokenToProvideAnotherUsingJwtToken(String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET_KEY))
                    .acceptExpiresAt(0)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (TokenExpiredException e) {
            throw new InvalidJwtToken("Token has expired. Please request a new one.");
        } catch (JWTVerificationException e) {
            throw new InvalidJwtToken("Invalid token. Please request a new one.");
        }
    }


    public static String validateTokenClaim(String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JWT_SECRET_KEY))
                    .acceptExpiresAt(0)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("use").asString();
        } catch (TokenExpiredException e) {
            throw new InvalidJwtToken("Token has expired. Please request a new one.");
        } catch (JWTVerificationException e) {
            throw new InvalidJwtToken("Invalid token. Please request a new one.");
        }
    }
}
