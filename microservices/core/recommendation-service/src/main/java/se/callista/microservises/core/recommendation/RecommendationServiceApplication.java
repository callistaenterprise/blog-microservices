package se.callista.microservises.core.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.UUID;

@SpringBootApplication
@EnableDiscoveryClient
public class RecommendationServiceApplication {

    public static void main(String[] args) {
        System.setProperty("MY_UUID", UUID.randomUUID().toString());
        SpringApplication.run(RecommendationServiceApplication.class, args);
    }
}
