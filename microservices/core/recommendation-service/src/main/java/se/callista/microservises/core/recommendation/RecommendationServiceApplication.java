package se.callista.microservises.core.recommendation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RecommendationServiceApplication {

    // FIXME: Remove me for later use...
    @Bean
    public ApplicationListener<EmbeddedServletContainerInitializedEvent> getSCListener() {

        return new ApplicationListener<EmbeddedServletContainerInitializedEvent>() {
            @Override
            public void onApplicationEvent(EmbeddedServletContainerInitializedEvent embeddedServletContainerInitializedEvent) {
                System.err.println("### GOT AN ESCIE: " + embeddedServletContainerInitializedEvent);
                EmbeddedServletContainer c = embeddedServletContainerInitializedEvent.getEmbeddedServletContainer();
                System.err.println("### PORT: " + c.getPort());
                System.err.println("### SET MY_UUDI: " + c.getPort());
                System.setProperty("MY_UUID", "" + c.getPort());
            }
        };
    }

    public static void main(String[] args) {
        String uuid = UUID.randomUUID().toString();
        System.err.println("### III. SKIP SETTING MY_UUDI: " + uuid);
        System.setProperty("MY_UUID", uuid);
        SpringApplication.run(RecommendationServiceApplication.class, args);
    }
}
