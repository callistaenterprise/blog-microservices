package se.callista.microservises.core.review;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.UUID;

@SpringBootApplication
@EnableDiscoveryClient
public class ReviewServiceApplication {

    public static void main(String[] args) {
        System.setProperty("MY_UUID", UUID.randomUUID().toString());
        SpringApplication.run(ReviewServiceApplication.class, args);
    }
}
