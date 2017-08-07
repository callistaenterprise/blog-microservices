package se.callista.microservices.core.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import se.callista.microservices.core.product.persistence.entity.ProductEntity;
import se.callista.microservices.core.product.persistence.repository.ProductRepository;

import javax.inject.Inject;
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

    private final ProductRepository repository;

    @Inject
    public ProductServiceApplication(ProductRepository repository) {
        this.repository = repository;
    }

    private static boolean initDone = false;

    @EventListener(ApplicationReadyEvent.class)
    public void insertSomeTestdata() {

        if (initDone) {
            LOG.warn("insertSomeTestdata() triggered by more than one ApplicationReadyEvent, skipping repeated processing!");
            return;
        }
        initDone = true;

        final int maxProductId = 10;
        final int sampleProductId = 7;

        LOG.info("Storing {} product entities...", maxProductId);

        for (int productId = 1; productId <= maxProductId; productId++) {
            String suffix = "-" + productId;
            ProductEntity savedEntity = repository.save(new ProductEntity(productId, "Name" + suffix, 1000 + productId)).block();
            LOG.debug("SavedEntity: {}", savedEntity);
        }

        LOG.info("Stored {} recommendation entities", repository.count().block());

        // TODO: Move to test class!!!
        repository.findAll().collectList().block().forEach(e -> LOG.info(" - " + e));

        LOG.info("Product for productId: {} = {}",
            sampleProductId,
            repository.findByProductId(sampleProductId).block());
    }
}
