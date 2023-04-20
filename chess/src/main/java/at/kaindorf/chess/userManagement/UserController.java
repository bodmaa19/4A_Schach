package at.kaindorf.chess.userManagement;

import at.kaindorf.chess.database.UserMockDatabase;
import at.kaindorf.chess.jwt.JWTNeeded;
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
            UserData userData = new UserData(userOptional.get().getUsername(), userOptional.get().getBestScore());
            String token = JWTUtil.generateToken(userData.getUsername());
            return ResponseEntity.ok().header("Authorization", token).body(userData);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @JWTNeeded
    @RequestMapping(method = RequestMethod.GET, path = "/userController/validToken")
    public ResponseEntity validToken()
    {
        return ResponseEntity.ok().build();
    }

    @CrossOrigin(origins = "http://localhost:4200")
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
*/
