package at.kaindorf.chess.userManagement;

import at.kaindorf.chess.database.UserMockDatabase;
import at.kaindorf.chess.jwt.JWTNeeded;
import at.kaindorf.chess.pojos.User;
import at.kaindorf.chess.pojos.UserData;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController()
public class UserController
{
    /*
     @RequestMapping(path = "/")
     // public String getGreeting()
     public ResponseEntity getGreeting()
     {
         // return "Das ist ein Test !!!";
         return ResponseEntity.ok("Das ist ein Test !!!");
     }
    */

    private UserMockDatabase userMockDatabase = UserMockDatabase.getInstance();

    @RequestMapping(method = RequestMethod.GET, path = "/userController/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllUsers()
    {
        List<User> userList = userMockDatabase.getUserList();
        if (userList.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userList);
    }

    public static final String JWT = "This-is-my-not-very-long-String-to-secure-all-the-user-data-!!!!!!!!!-123456789-000000000";

    public String createJWTToken(String payload) throws JOSEException
    {
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(payload));
        jwsObject.sign(new MACSigner(JWT.getBytes()));
        return jwsObject.serialize();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/userController/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity register(@RequestBody User user)
    {
        Optional<User> userOptional = userMockDatabase.register(user);
        if (userOptional.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        return ResponseEntity.accepted().build();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/userController/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody User user)
    {
        System.out.printf(user.toString());
        Optional<User> userOptional = userMockDatabase.login(user);
        try
        {
            if(userOptional.isEmpty())
            {
                throw new Exception("not verified");
            }
            UserData userData = new UserData(userOptional.get().getUsername(), userOptional.get().getBestScore());
            String token = createJWTToken(userData.getUsername());
            return ResponseEntity.ok().header("Authorization", token).body(userData);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @JWTNeeded
    @RequestMapping(method = RequestMethod.GET, path = "/userController/validToken")
    public ResponseEntity validToken()
    {
        return ResponseEntity.ok().build();
    }

    @JWTNeeded
    @RequestMapping(method = RequestMethod.PATCH, path = "/userController/bestScore", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateBestScore(@RequestBody UserData userData)
    {
        User user = new User(userData.getUsername(), null, userData.getBestScore());
        Optional<User> userOptional = userMockDatabase.updateBestScore(user);
        if (userOptional.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userData);
    }
}
