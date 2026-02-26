package mopl.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "mopl")
public class MoplApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoplApiApplication.class, args);
    }

}
