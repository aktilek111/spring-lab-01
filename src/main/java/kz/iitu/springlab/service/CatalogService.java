package kz.iitu.springlab.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import kz.iitu.springlab.audit.Audited;
import kz.iitu.springlab.metrics.Measured;

@Service
public class CatalogService {

    // Task 4.5: self reference. @Lazy is needed, otherwise Spring Boot
    // reports a circular dependency (the bean injects itself).
    @Autowired
    @Lazy
    private CatalogService self;

    @Measured
    public String findById(long id) {
        sleep(50); // simulating a database call
        return "Item no. " + id;
    }

    @Audited(action = "CATALOG_LIST", logArguments = true)
    @Measured
    public List<String> findAll(int limit) {
        sleep(300); // a deliberately «slow» method
        return IntStream.rangeClosed(1, limit)
                .mapToObj(i -> "Item no. " + i)
                .toList();
    }

    @Audited(action = "CATALOG_REMOVE")
    public String remove(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid identifier: " + id);
        }
        return "Removed item no. " + id;
    }

    // Task 4.3: self-invocation BEFORE the fix (calls go via `this`, past the proxy)
    public String removeTwice(long id) {
        String first = remove(id);
        String second = remove(id + 1);
        return first + "; " + second;
    }

    // Task 4.5: self-invocation AFTER the fix (calls go through the proxy).
    // To compare: call /remove-twice/{id} (broken) and /remove-twice-fixed/{id}.
    public String removeTwiceFixed(long id) {
        String first = self.remove(id);
        String second = self.remove(id + 1);
        return first + "; " + second;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
