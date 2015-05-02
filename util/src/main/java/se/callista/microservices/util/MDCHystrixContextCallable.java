package se.callista.microservices.util;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by magnus on 02/05/15.
 */
public class MDCHystrixContextCallable<K> implements Callable {

    private final Callable<K> actual;
    private final Map parentMDC;

    public MDCHystrixContextCallable(Callable<K> actual) {
        System.err.println("### " + tn() + " Init MDCHystrixContextCallable...");
        this.actual = actual;
        this.parentMDC = MDC.getCopyOfContextMap();
    }

    @Override
    public K call() throws Exception {
        System.err.println("### " + tn() + " Call using MDCHystrixContextCallable...");
        Map childMDC = MDC.getCopyOfContextMap();

        try {
            MDC.setContextMap(parentMDC);
            return actual.call();
        } finally {
            MDC.setContextMap(childMDC);
        }
    }

    private String tn() {
        return Thread.currentThread().getName();
    }

}
