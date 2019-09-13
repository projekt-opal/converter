package org.diceresearch.webservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class WebservicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebservicesApplication.class, args);
    }

}
