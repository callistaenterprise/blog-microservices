package se.callista.microservices.composite.product;

import com.codahale.metrics.MetricRegistry;
import com.readytalk.metrics.StatsDReporter;
import com.netflix.hystrix.strategy.HystrixPlugins;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import se.callista.microservices.util.MDCHystrixConcurrencyStrategy;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableCircuitBreaker
@EnableDiscoveryClient
@EnableResourceServer
@ComponentScan({"se.callista.microservices.composite.product", "se.callista.microservices.util"})
public class ProductCompositeServiceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceApplication.class);

    static {
        // for localhost testing only
        LOG.warn("Will now disable hostname check in SSL, only to be used during development");
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);
    }

    @Value("${app.rabbitmq.host:localhost}")
    String rabbitMqHost;

    @Inject
    MetricRegistry registry;

    @PostConstruct
    public void postInject() {
        LOG.info("Register a StatsD Metrics Reporter");
        StatsDReporter.forRegistry(registry)
            .prefixedWith("composite-service")
            .build("graphite", 8125)
            .start(1, TimeUnit.SECONDS);
        LOG.info("Registration of a StatsD Metrics Reporter done!");
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        LOG.info("Create RabbitMqCF for host: {}", rabbitMqHost);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitMqHost);
        return connectionFactory;
    }

    public static void main(String[] args) {
        LOG.info("Register MDCHystrixConcurrencyStrategy");
        HystrixPlugins.getInstance().registerConcurrencyStrategy(new MDCHystrixConcurrencyStrategy());
        SpringApplication.run(ProductCompositeServiceApplication.class, args);
    }
}
