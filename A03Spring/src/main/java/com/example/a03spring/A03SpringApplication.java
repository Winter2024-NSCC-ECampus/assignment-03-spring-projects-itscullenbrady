package com.example.a03spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.EnableCommand;

@SpringBootApplication
@EnableCommand
public class A03SpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(A03SpringApplication.class, args);
	}
}