package com.meta1203.screenshirt;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenshirtApplication {
	public static final Executor EXECUTOR = Executors.newWorkStealingPool();
	
	public static void main(String[] args) {
		SpringApplication.run(ScreenshirtApplication.class, args);
	}

}
