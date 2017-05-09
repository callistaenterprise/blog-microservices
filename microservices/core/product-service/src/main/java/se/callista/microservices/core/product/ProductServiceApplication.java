package se.callista.microservices.core.product;

import com.netflix.discovery.DiscoveryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import javax.net.ssl.HttpsURLConnection;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"se.callista.microservices.core.product", "se.callista.microservices.util"})
public class ProductServiceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceApplication.class);

    static {
        // for localhost testing only
        LOG.warn("Will now disable hostname check in SSL, only to be used during development");
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(ProductServiceApplication.class, args);

        LOG.info("Connected to RabbitMQ at: {}", ctx.getEnvironment().getProperty("spring.rabbitmq.host"));
    }
}
