package com.example.apigmac;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootApplication
public class ApigmacApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApigmacApplication.class, args);
	}

//	@Bean
//	CommandLineRunner testarS3(S3Client s3Client) {
//		return args -> {
//			s3Client.listBuckets().buckets()
//					.forEach(b -> System.out.println(b.name()));
//		};
//	}

}
