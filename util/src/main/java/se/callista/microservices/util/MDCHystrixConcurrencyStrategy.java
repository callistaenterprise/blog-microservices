package se.callista.microservices.util;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;

import java.util.concurrent.Callable;

/**
 * Created by magnus on 02/05/15.
 */
public class MDCHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new MDCHystrixContextCallable<>(callable);
    }
}
