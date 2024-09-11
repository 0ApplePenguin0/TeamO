package com.example.workhive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WorkHiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkHiveApplication.class, args);
    }

}
