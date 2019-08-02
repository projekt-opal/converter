package org.diceresearch.opalconfirmconversionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;

@SpringBootApplication
@EnableEurekaClient
@EnableBinding(Processor.class)
public class OpalConfirmConversionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpalConfirmConversionServiceApplication.class, args);
    }

}
