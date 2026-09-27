package com.example.memberboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MemberboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(MemberboardApplication.class, args);
	}

}
