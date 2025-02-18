package com.example.music_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.example")
public class MusicSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusicSystemApplication.class, args);
    }
}
