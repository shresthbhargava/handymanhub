package com.handymanhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class HandymanHubApplication {

	private static final Logger log = LoggerFactory.getLogger(HandymanHubApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(HandymanHubApplication.class, args);
		log.info("HandymanHub API is up — connecting skilled hands to those who need them.");
	}
}