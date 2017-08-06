package se.callista.microservices.core.recommendation;

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
import se.callista.microservices.core.recommendation.persistence.entity.RecommendationEntity;
import se.callista.microservices.core.recommendation.persistence.repository.RecommendationRepository;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import java.util.List;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"se.callista.microservices.core.recommendation", "se.callista.microservices.util"})
public class RecommendationServiceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceApplication.class);

    static {
        // for localhost testing only
        LOG.warn("Will now disable hostname check in SSL, only to be used during development");
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(RecommendationServiceApplication.class, args);

        LOG.info("Connected to RabbitMQ at: {}", ctx.getEnvironment().getProperty("spring.rabbitmq.host"));
    }

    private final RecommendationRepository repository;

    @Inject
    public RecommendationServiceApplication(RecommendationRepository repository) {
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
        final int maxRecommendationId = 3;
        final int sampleProductId = 7;

        LOG.info("Storing {} recommendation entities...", maxProductId * maxRecommendationId);

        for (int productId = 1; productId <= maxProductId; productId++) {
            for (int recommendationId = 1; recommendationId <= maxRecommendationId; recommendationId++) {
                String suffix = "-" + productId + "." + recommendationId;
                int rate = 10 * productId + recommendationId;
                RecommendationEntity savedEntity = repository.save(new RecommendationEntity(productId, recommendationId, "Author" + suffix, rate, "Content" + suffix)).block();
                LOG.debug("SavedEntity: {}", savedEntity);
            }
        }

        LOG.info("Stored {} recommendation entities", repository.count().block());

        // TODO: Move to test class!!!
        repository.findAll().collectList().block().forEach(e -> LOG.info(" - " + e));

        LOG.info("Recommendations for productId: {}", sampleProductId);
        repository.findByProductId(sampleProductId).collectList().block().forEach(e -> LOG.info(" - " + e));
    }
}
