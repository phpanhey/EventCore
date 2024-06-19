package de.panhey.eventcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventCoreApplication.class, args);

    }

}
