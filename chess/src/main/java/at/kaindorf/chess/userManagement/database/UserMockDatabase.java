package at.kaindorf.chess.userManagement.database;

import at.kaindorf.chess.userManagement.io.IO_Access;
import at.kaindorf.chess.userManagement.pojos.User;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/**
 * project name: Schach_4A
 * author: Manuel Bodlos
 * date: 05.04.2023
 */
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
        int nextId;
        if(userList.isEmpty()){
            nextId = 1;
        }else{
            nextId = userList.stream().mapToInt(i -> i.getUserId()).max().getAsInt() + 1;
        }
        user.setUserId(nextId);
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
