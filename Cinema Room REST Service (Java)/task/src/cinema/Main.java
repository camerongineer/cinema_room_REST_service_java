package cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public final static String PASSWORD = "super_secret";

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
