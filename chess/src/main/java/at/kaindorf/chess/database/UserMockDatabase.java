package at.kaindorf.chess.database;

import at.kaindorf.chess.io.IO_Access;
import at.kaindorf.chess.pojos.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserMockDatabase
{
    private static UserMockDatabase theInstance = null;
    private List<User> userList = new ArrayList<>();

    public static UserMockDatabase getInstance()
    {
        if (theInstance == null)
        {
            theInstance = new UserMockDatabase();
        }
        return theInstance;
    }

    private UserMockDatabase()
    {
        try
        {
            userList = IO_Access.readUserFromJson();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<User> getUserList()
    {
        return userList;
    }

    public Optional<User> register(User user)
    {
        if (userList.contains(user))
        {
            return Optional.empty();
        }
        user.setBestScore(Integer.valueOf(0));
        userList.add(user);
        try
        {
            IO_Access.writeUserToJson(userList);
        } catch (IOException | URISyntaxException e)
        {
            throw new RuntimeException(e);
        }
        return Optional.of(user);
    }

    public Optional<User> login(User user)
    {
        System.out.printf(userList.toString());
        return userList.stream().filter(u -> u.getUsername().equals(user.getUsername()) && u.getPassword().equals(user.getPassword())).findFirst();
    }

    public Optional<User> updateBestScore(User user)
    {
        Optional<User> userOptional = userList.stream().filter(u -> u.equals(user)).findFirst();
        if (!userOptional.isEmpty())
        {
            userOptional.get().setBestScore(user.getBestScore());
            try
            {
                IO_Access.writeUserToJson(userList);
            } catch (IOException | URISyntaxException e)
            {
                throw new RuntimeException(e);
            }
        }
        return userOptional;
    }
}
