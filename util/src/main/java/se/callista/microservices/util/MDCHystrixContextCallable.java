package se.callista.microservices.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by magnus on 02/05/15.
 */
public class MDCHystrixContextCallable<K> implements Callable {

    private static final Logger LOG = LoggerFactory.getLogger(MDCHystrixContextCallable.class);

    private final Callable<K> actual;
    private final Map parentMDC;

    public MDCHystrixContextCallable(Callable<K> actual) {
        LOG.debug("Init MDCHystrixContextCallable...");
        this.actual = actual;
        this.parentMDC = MDC.getCopyOfContextMap();
    }

    @Override
    public K call() throws Exception {
        LOG.debug("Call using MDCHystrixContextCallable...");
        Map childMDC = MDC.getCopyOfContextMap();

        try {
            MDC.setContextMap(parentMDC);
            return actual.call();
        } finally {
            MDC.setContextMap(childMDC);
        }
    }
}