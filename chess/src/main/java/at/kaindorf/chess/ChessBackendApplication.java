package at.kaindorf.chess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * project name: Schach_4A
 * author: Roman Lanschützer
 * date: 24.03.2023
 */
@SpringBootApplication
public class ChessBackendApplication {

	public static void main(String[] args) {
		//BookIO.deleteOverhead();
		SpringApplication.run(ChessBackendApplication.class, args);
	}

}
