package at.kaindorf.chess.io;

import at.kaindorf.chess.pojos.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

public class IO_Access
{
    public static List<User> readUserFromJson() throws IOException
    {
        Path path = Path.of("D:\\POS4\\PROJECT\\HELP\\schach_sb\\src\\main\\resources\\user.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(path.toFile(), new TypeReference<List<User>>(){});
    }

    public static void writeUserToJson(List<User> userList) throws IOException, URISyntaxException
    {
        Path path = Path.of("D:\\POS4\\PROJECT\\HELP\\schach_sb\\src\\main\\resources\\user.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), userList);
    }
}
