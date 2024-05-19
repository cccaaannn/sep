package com.kurtcan.sepsearchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SepSearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SepSearchServiceApplication.class, args);
	}

}
