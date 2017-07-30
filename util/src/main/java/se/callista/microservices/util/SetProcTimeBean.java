package se.callista.microservices.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class SetProcTimeBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(SetProcTimeBean.class);

    private int minMs;
    private int maxMs;

    @Autowired
    public SetProcTimeBean(
        @Value("${service.defaultMinMs:0}") int minMs,
        @Value("${service.defaultMaxMs:0}") int maxMs) {

        setDefaultProcessingTime(minMs, maxMs);
    }

    public void setDefaultProcessingTime(int minMs, int maxMs) {

        if (minMs < 0) {
            minMs = 0;
        }
        if (maxMs < minMs) {
            maxMs = minMs;
        }

        this.minMs = minMs;
        this.maxMs = maxMs;
        LOG.info("Set response time to {} - {} ms.", this.minMs, this.maxMs);
    }

    public int calculateProcessingTime() {
        int processingTimeMs = minMs + (int) (Math.random() * (maxMs - minMs));
        LOG.debug("Return calculated processing time: {} ms", processingTimeMs);
        return processingTimeMs;
    }
}