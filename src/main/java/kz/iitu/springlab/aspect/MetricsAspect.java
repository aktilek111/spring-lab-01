package kz.iitu.springlab.aspect;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import kz.iitu.springlab.metrics.Measured;

/**
 * Variant 11: metrics by annotation.
 * Accumulates the call count and the total time per @Measured method.
 */
@Aspect
@Component
@Order(4) // innermost: it measures the method itself, not the other aspects
public class MetricsAspect {

    private static final class Stat {
        final LongAdder calls = new LongAdder();
        final LongAdder totalNanos = new LongAdder();
    }

    private final Map<String, Stat> stats = new ConcurrentHashMap<>();

    // "measured" in the expression is bound to the parameter of the same name
    @Around("@annotation(measured)")
    public Object collect(ProceedingJoinPoint pjp, Measured measured) throws Throwable {
        String name = measured.value().isBlank()
                ? pjp.getSignature().toShortString()
                : measured.value();
        long started = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            Stat stat = stats.computeIfAbsent(name, k -> new Stat());
            stat.calls.increment();
            stat.totalNanos.add(System.nanoTime() - started);
        }
    }

    /** Snapshot for the endpoint: name -> {calls, totalMs, avgMs}. */
    public Map<String, Map<String, Object>> summary() {
        Map<String, Map<String, Object>> result = new TreeMap<>();
        stats.forEach((name, stat) -> {
            long calls = stat.calls.sum();
            double totalMs = stat.totalNanos.sum() / 1_000_000.0;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("calls", calls);
            row.put("totalMs", Math.round(totalMs * 100) / 100.0);
            row.put("avgMs", calls == 0 ? 0.0 : Math.round(totalMs / calls * 100) / 100.0);
            result.put(name, row);
        });
        return result;
    }
}
