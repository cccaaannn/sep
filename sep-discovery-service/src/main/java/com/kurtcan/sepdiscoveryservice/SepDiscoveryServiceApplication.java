package com.kurtcan.sepdiscoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SepDiscoveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SepDiscoveryServiceApplication.class, args);
	}

}
