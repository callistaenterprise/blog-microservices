package se.callista.microservices.core.product;

import com.netflix.discovery.DiscoveryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"se.callista.microservices.core.product", "se.callista.microservices.util"})
public class ProductServiceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceApplication.class);

//    @Inject
//    MetricRegistry registry;
//
//    @PostConstruct
//    public void postInject() {
//        LOG.info("Register a StatsD Metrics Reporter");
//        StatsDReporter.forRegistry(registry)
//            .prefixedWith("product-service")
//            .build("statsd", 8125)
//            .start(1, TimeUnit.SECONDS);
//        LOG.info("Registration of a StatsD Metrics Reporter done!");
//    }

//    @Value("${app.rabbitmq.host:localhost}")
//    String rabbitMqHost;
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        LOG.info("Create RabbitMqCF for host: {}", rabbitMqHost);
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitMqHost);
//        return connectionFactory;
//    }

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);

        LOG.info("Register ShutdownHook");
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                LOG.info("Shutting down, unregister from Eureka!");
                DiscoveryManager.getInstance().shutdownComponent();
            }
        });
    }
}
