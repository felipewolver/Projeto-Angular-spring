package com.algamoneyfel.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.algamoneyfel.api.config.property.AlgamoneyFelApiProperty;

@SpringBootApplication
@EnableConfigurationProperties(AlgamoneyFelApiProperty.class)
public class AlgamoneyfelApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgamoneyfelApiApplication.class, args);
	}

}
