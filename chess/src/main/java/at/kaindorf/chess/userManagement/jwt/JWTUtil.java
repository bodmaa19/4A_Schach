package at.kaindorf.chess.userManagement.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTUtil
{
    private static final String JWT_SECRET = "This-is-my-secret-to-encrypt-all-the-data-from-the-user-to-secure-our-system-!";

    public static String generateToken(String username)
    {
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        JWTCreator.Builder creator = JWT.create().withIssuer("auth0").withSubject(username);
        return creator.sign(algorithm);
    }

    public static boolean verifyToken(String token)
    {
        try
        {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").build();
            verifier.verify(token);
            return true;
        }
        catch (JWTVerificationException exception)
        {
            return false;
        }
    }
}