package com.kurtcan.sepconfigservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class SepConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SepConfigServiceApplication.class, args);
	}

}
