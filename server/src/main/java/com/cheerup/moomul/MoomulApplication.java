package com.cheerup.moomul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = {
	@Server(url = "/api", description = "Default Server URL")
})
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class MoomulApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoomulApplication.class, args);
	}

}
