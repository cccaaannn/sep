package com.kurtcan.sepproductservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SepProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SepProductServiceApplication.class, args);
	}

}
