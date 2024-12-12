package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@Slf4j
@ComponentScan
public class FilmorateApplication {
    public static void main(String[] args) {
        log.info("Starting Filmorate Application!");
        SpringApplication.run(FilmorateApplication.class, args);
    }

}
