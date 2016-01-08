//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.springframework.cloud.netflix.hystrix;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsPoller;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsPoller.MetricsAsJsonPollerListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.cloud.client.actuator.NamedFeature;
import org.springframework.cloud.netflix.hystrix.HystrixStreamEndpoint;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HystrixCircuitBreakerConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(HystrixCircuitBreakerConfiguration.class);

    public HystrixCircuitBreakerConfiguration() {
    }

    @Bean
    public HystrixCommandAspect hystrixCommandAspect() {
        return new HystrixCommandAspect();
    }

    @Bean
    public HystrixCircuitBreakerConfiguration.HystrixShutdownHook hystrixShutdownHook() {
        return new HystrixCircuitBreakerConfiguration.HystrixShutdownHook();
    }

    @Bean
    public HasFeatures hystrixFeature() {
        return HasFeatures.namedFeatures(new NamedFeature[]{new NamedFeature("Hystrix", HystrixCommandAspect.class)});
    }

    private class HystrixShutdownHook implements DisposableBean {
        private HystrixShutdownHook() {
        }

        public void destroy() throws Exception {
            Hystrix.reset();
        }
    }

    @Configuration
    @ConditionalOnClass({HystrixMetricsPoller.class, GaugeService.class})
    protected static class HystrixMetricsPollerConfiguration implements SmartLifecycle {
        private static Log logger = LogFactory.getLog(HystrixCircuitBreakerConfiguration.HystrixMetricsPollerConfiguration.class);
        @Autowired(
                required = false
        )
        @Qualifier("dropwizardMetricServices")
        private GaugeService gauges;
        private ObjectMapper mapper = new ObjectMapper();
        private HystrixMetricsPoller poller;
        private Set<String> reserved = new HashSet(Arrays.asList(new String[]{"group", "name", "type", "currentTime"}));

        protected HystrixMetricsPollerConfiguration() {
            LOG.debug("ML FIX for HystrixMetricsPollerConfiguration APPLIED!");
        }

        public void start() {
            if(this.gauges != null) {
                MetricsAsJsonPollerListener listener = new MetricsAsJsonPollerListener() {
                    public void handleJsonMetric(String json) {
                        try {
                            Map ex = (Map)HystrixMetricsPollerConfiguration.this.mapper.readValue(json, Map.class);
                            if(ex != null && ex.containsKey("type")) {
                                HystrixMetricsPollerConfiguration.this.addMetrics(ex, "hystrix.");
                            }
                        } catch (IOException var3) {
                            ;
                        }

                    }
                };
                this.poller = new HystrixMetricsPoller(listener, 2000);
                this.poller.start();
                logger.info("Starting poller");
            }
        }

        private void addMetrics(Map<String, Object> map, String root) {
            StringBuilder prefixBuilder = new StringBuilder(root);
            if(map.containsKey("type")) {
                prefixBuilder.append((String)map.get("type"));
                if(map.containsKey("group")) {
                    prefixBuilder.append(".").append(map.get("group"));
                }

                prefixBuilder.append(".").append(map.get("name"));
            }

            String prefix = prefixBuilder.toString();
            Iterator var5 = map.keySet().iterator();

            while(var5.hasNext()) {
                String key = (String)var5.next();
                Object value = map.get(key);
                if(!this.reserved.contains(key)) {
                    if(value instanceof Number) {
                        String sub = prefix + "." + key;
                        this.gauges.submit(sub, ((Number)value).doubleValue());
                    } else if(value instanceof Map) {
                        Map sub1 = (Map)value;
                        this.addMetrics(sub1, prefix);
                    }
                }
            }

        }

        public void stop() {
            if(this.poller != null) {
                this.poller.shutdown();
            }

        }

        public boolean isRunning() {
            return this.poller != null?this.poller.isRunning():false;
        }

        public int getPhase() {
            return 2147483647;
        }

        public boolean isAutoStartup() {
            return true;
        }

        public void stop(Runnable callback) {
            if(this.poller != null) {
                this.poller.shutdown();
            }

            callback.run();
        }
    }

    @Configuration
    @ConditionalOnProperty(
            value = {"hystrix.stream.endpoint.enabled"},
            matchIfMissing = true
    )
    @ConditionalOnWebApplication
    @ConditionalOnClass({Endpoint.class, HystrixMetricsStreamServlet.class})
    protected static class HystrixWebConfiguration {
        protected HystrixWebConfiguration() {
        }

        @Bean
        public HystrixStreamEndpoint hystrixStreamEndpoint() {
            return new HystrixStreamEndpoint();
        }

        @Bean
        public HasFeatures hystrixFeature() {
            return HasFeatures.namedFeature("Hystrix Stream Servlet", HystrixStreamEndpoint.class);
        }
    }
}
