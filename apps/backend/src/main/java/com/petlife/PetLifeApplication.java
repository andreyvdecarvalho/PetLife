package com.petlife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PetLifeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetLifeApplication.class, args);
    }
}
