package se.callista.microservices.composite.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.UUID;

@SpringBootApplication
@EnableCircuitBreaker
@EnableDiscoveryClient
public class ProductCompositeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductCompositeServiceApplication.class, args);
    }
}
