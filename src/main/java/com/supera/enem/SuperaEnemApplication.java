package com.supera.enem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class SuperaEnemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuperaEnemApplication.class, args);
	}

}
