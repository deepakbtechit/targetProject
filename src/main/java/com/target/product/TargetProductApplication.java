package com.target.targetProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class TargetProjectApplication {
	public static void main(String[] args) {
		SpringApplication.run(TargetProjectApplication.class, args);
	}
}
