package at.kaindorf.chess.userManagement;

import at.kaindorf.chess.database.UserMockDatabase;
import at.kaindorf.chess.jwt.JWTUtil;
import at.kaindorf.chess.pojos.User;
import at.kaindorf.chess.pojos.UserData;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController()
public class UserController
{
    private UserMockDatabase userMockDatabase = UserMockDatabase.getInstance();

    @CrossOrigin(origins = "http://localhost:4200")
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

    @CrossOrigin(origins = "http://localhost:4200")
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

    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(method = RequestMethod.POST, path = "/userController/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity login(@RequestBody User user)
    {
        Optional<User> userOptional = userMockDatabase.login(user);
        try
        {
            if(userOptional.isEmpty())
            {
                throw new Exception("not verified");
            }
            String token = JWTUtil.generateToken(userOptional.get().getUsername());
            UserData userData = new UserData(userOptional.get().getUsername(), userOptional.get().getBestScore(), token);
            return ResponseEntity.ok().header("Authorization", token).body(userData);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    // @JWTNeeded
    @RequestMapping(method = RequestMethod.GET, path = "/userController/validToken")
    public ResponseEntity validToken(@RequestHeader(value = "Authorization") String token)
    {
        if (JWTUtil.verifyToken(token))
        {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    // @JWTNeeded
    @RequestMapping(method = RequestMethod.PATCH, path = "/userController/bestScore", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateBestScore(@RequestHeader(value = "Authorization") String token, @RequestBody UserData userData)
    {
        if (JWTUtil.verifyToken(token))
        {
            User user = new User(userData.getUsername(), null, userData.getBestScore());
            Optional<User> userOptional = userMockDatabase.updateBestScore(user);
            if (userOptional.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(userData);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

/*
     @RequestMapping(path = "/")
     // public String getGreeting()
     public ResponseEntity getGreeting()
     {
         // return "Das ist ein Test !!!";
         return ResponseEntity.ok("Das ist ein Test !!!");
     }
*/

/*
    // Erstellen eines Tokens
    String token = JwtUtil.generateToken("username");

    // Überprüfen des Tokens
    if (JwtUtil.verifyToken(token))
    {
        // Token ist gültig
    }
    else
    {
        // Token ist ungültig
    }

    @RequestHeader(value = "Authorization") String token
    @RequestBody UserData userData
    @PathVariable Long id_1
    @RequestParam Long id_2
*/
