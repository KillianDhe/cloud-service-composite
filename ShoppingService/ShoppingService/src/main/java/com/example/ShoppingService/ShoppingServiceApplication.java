package com.example.ShoppingService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;

@SpringBootApplication
public class ShoppingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingServiceApplication.class, args);
		System.out.println("version: " + SpringVersion.getVersion());

	}

}
