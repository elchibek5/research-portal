package com.haritara.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing  // Enable JPA auditing for @CreatedDate and @LastModifiedDate (Critical #1)
public class ResearchPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResearchPortalApplication.class, args);
	}

}


