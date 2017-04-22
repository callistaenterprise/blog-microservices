package se.callista.microservices.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class CpuCruncherBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(CpuCruncherBean.class);

    private int minStrength = 0;
    private int maxStrength = 0;

    @Value("${service.minStrength:0}")
    public void setMinStrength(int minStrength) {

        if (minStrength < 0) {
            minStrength = 0;
        }

        LOG.info("Set min strength to {}.", minStrength);
        this.minStrength = minStrength;
    }

    @Value("${service.maxStrength:0}")
    public void setMaxStrength(int maxStrength) {

        if (maxStrength < 0) {
            maxStrength = 0;
        }

        LOG.info("Set max strength time to {}.", maxStrength);
        this.maxStrength = maxStrength;
    }

    public void exec() {

        int strength = calculateStrength();

        LOG.debug("Will encrypt quote using BCrypt with {} log rounds (i.e. strength)", strength);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(strength);

        String quoteText = "This is a very secret message...";
        String encryptedQuote = encoder.encode(quoteText);

        LOG.debug("Encrypted quote: '" + encryptedQuote + "'");
        LOG.debug("Delivered quote: '" + quoteText + "'");

    }

    public int calculateStrength() {

        if (maxStrength < minStrength) {
            maxStrength = minStrength;
            LOG.info("The max strength was set lower then the min strength, max strength now set equal to the min strength: {}.", minStrength);
        }

        int strength = minStrength + (int) (Math.random() * (maxStrength - minStrength));
        LOG.debug("Calculated strength: {}", strength);
        return strength;
    }
}