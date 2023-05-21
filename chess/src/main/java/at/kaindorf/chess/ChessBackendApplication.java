package at.kaindorf.chess;

import at.kaindorf.chess.ai.book.BookIO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChessBackendApplication {

	public static void main(String[] args) {
		//BookIO.deleteOverhead();
		SpringApplication.run(ChessBackendApplication.class, args);
	}

}
