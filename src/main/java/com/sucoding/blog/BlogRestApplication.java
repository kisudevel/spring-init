package com.sucoding.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BlogRestApplication {

    static void main(String[] args) {
        SpringApplication.run(BlogRestApplication.class, args);
    }

}
