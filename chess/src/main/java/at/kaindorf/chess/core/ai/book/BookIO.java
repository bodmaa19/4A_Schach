package at.kaindorf.chess.core.ai.book;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 * project name: Schach_4A
 * author: Roman Lanschützer
 * date: 21.05.2023
 */

public class BookIO {
    public static final Path oldBookPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "originalBook.txt");
    public static final Path bookPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "newBook.txt");
    public static final Random rand = new Random();

    public static void deleteOverhead(){
        try {
            List<String> lines = Files.readAllLines(oldBookPath);
            for (int i = 0; i < lines.size()-1; i++) {
                if(!lines.get(i).startsWith("1.") || lines.get(i).startsWith("1.d4 g6") || lines.get(i).startsWith("1.e4 g6")){
                    lines.remove(i);
                    i--;
                }
            }
            Files.write(bookPath, lines);
            System.out.println("Book loaded");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getNextMove(String moveString){
        try {
            List<String> lines = Files.readAllLines(bookPath);
            for (int i = 0; i < lines.size()-1; i++) {
                if(!lines.get(i).startsWith(moveString)){
                    lines.remove(i);
                    i--;
                }
            }
            String line = lines.get(rand.nextInt(lines.size())).replace(moveString, "");
            System.out.println(line);
            line = line.substring(0,line.indexOf(' '));
            return line;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        BookIO.deleteOverhead();
    }
}
