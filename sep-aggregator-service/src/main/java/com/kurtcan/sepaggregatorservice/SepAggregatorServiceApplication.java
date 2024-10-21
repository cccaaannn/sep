package com.kurtcan.sepaggregatorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SepAggregatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SepAggregatorServiceApplication.class, args);
    }

}
