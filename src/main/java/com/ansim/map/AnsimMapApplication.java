package com.ansim.map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AnsimMapApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnsimMapApplication.class, args);
	}

}
