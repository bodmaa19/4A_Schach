package at.kaindorf.chess.jwt;

import at.kaindorf.chess.userManagement.UserController;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACVerifier;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@JWTNeeded
@Provider
@Priority(Priorities.AUTHORIZATION)
public class JWTNeededFilter implements ContainerRequestFilter
{
    @Override
    public void filter(ContainerRequestContext crc) throws IOException
    {
//        String token = crc.getHeaderString(HttpHeaders.AUTHORIZATION);
//        try
//        {
//            JWSObject jwsObject = JWSObject.parse(token);
//            if (!jwsObject.verify(new MACVerifier(UserController.JWT.getBytes())))
//            {
//                throw new Exception("not verified");
//            }
//        } catch (Exception e)
//        {
//            crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
//        }
    }
}
