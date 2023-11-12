package com.springben.javak8s;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JavaK8sApplication {

	@GetMapping
	public String message(){
		return "Hello Benny! This is hosted on Kubernetes using Argo CD. Yay! :)";
	}

	public static void main(String[] args) {
		SpringApplication.run(JavaK8sApplication.class, args);
	}

}
