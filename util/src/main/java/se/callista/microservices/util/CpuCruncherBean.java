package se.callista.microservices.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@RefreshScope
@Component
public class CpuCruncherBean {
    
    private static final Logger LOG = LoggerFactory.getLogger(CpuCruncherBean.class);

    private final int minStrength;
    private final int maxStrength;

    @Autowired
    public CpuCruncherBean(
        @Value("${service.minStrength:0}") int minStrength,
        @Value("${service.maxStrength:0}") int maxStrength) {

        if (minStrength < 0) {
            LOG.info("The min strength was set to a negative number, min strength now set 0, was: {}.", minStrength);
            minStrength = 0;
        }

        if (maxStrength < 0) {
            LOG.info("The max strength was set to a negative number, max strength now set 0, was: {}.", maxStrength);
            maxStrength = 0;
        }

        if (maxStrength < minStrength) {
            maxStrength = minStrength;
            LOG.info("The max strength was set lower then the min strength, max strength now set equal to the min strength: {}.", minStrength);
        }

        LOG.info("Set min and max strength to {} - {}.", minStrength, maxStrength);

        this.minStrength = minStrength;
        this.maxStrength = maxStrength;
    }

    public void exec() {

        int strength = calculateStrength();

        // Min allowed strength by bcrypt is 4, skip encryption if less
        if (strength < 4) {
            LOG.debug("Strength set lower then min value (4): {}, skip encryption)", strength);
            return;
        }

        LOG.debug("Will encrypt quote using BCrypt with {} log rounds (i.e. strength)", strength);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(strength);

        String quoteText = "This is a very secret message...";
        String encryptedQuote = encoder.encode(quoteText);

        LOG.debug("Encrypted quote: '" + encryptedQuote + "'");
        LOG.debug("Delivered quote: '" + quoteText + "'");
    }

    public int calculateStrength() {

        int strength = minStrength + (int) (Math.random() * (maxStrength - minStrength));
        LOG.debug("Calculated strength: {}", strength);
        return strength;
    }
}