package se.callista.microservices.core.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import se.callista.microservices.core.review.persistence.entity.ReviewEntity;
import se.callista.microservices.core.review.persistence.repository.ReviewRepository;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"se.callista.microservices.core.review", "se.callista.microservices.util"})
public class ReviewServiceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceApplication.class);

    static {
        // for localhost testing only
        LOG.warn("Will now disable hostname check in SSL, only to be used during development");
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(ReviewServiceApplication.class, args);

        LOG.info("Connected to RabbitMQ at: {}", ctx.getEnvironment().getProperty("spring.rabbitmq.host"));
    }

    private final ReviewRepository repository;

    private final Integer connectionPoolSize;

    @Inject
    public ReviewServiceApplication(
        @Value("${spring.datasource.maximum-pool-size:10}") Integer connectionPoolSize,
        ReviewRepository repository) {

        this.connectionPoolSize = connectionPoolSize;
        this.repository = repository;
    }

    @Bean
    public Scheduler jdbcScheduler() {
        LOG.info("Creates a jdbcScheduler with connectionPoolSize = " + connectionPoolSize);
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
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
        final int maxReviewId = 3;
        final int sampleProductId = 7;

        LOG.info("Storing {} review entities...", maxProductId * maxReviewId);

        for (int productId = 1; productId <= maxProductId; productId++) {
            for (int reviewId = 1; reviewId <= maxReviewId; reviewId++) {
                String suffix = "-" + productId + "." + reviewId;
                repository.save(new ReviewEntity(productId, reviewId, "Author" + suffix, "Subject" + suffix, "Content" + suffix));
            }
        }

        LOG.info("Stored {} review entities", repository.count());

        // TODO: Move to test class!!!
        repository.findAll().forEach(e -> LOG.info(" - " + e));

        LOG.info("Reviews for productId: {}", sampleProductId);
        repository.findByProductId(sampleProductId).forEach(e -> LOG.info(" - " + e));
    }
}
