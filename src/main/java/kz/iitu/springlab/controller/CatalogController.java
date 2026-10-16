package kz.iitu.springlab.controller;

import java.util.List;
import java.util.Map;

import org.springframework.aop.support.AopUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kz.iitu.springlab.aspect.MetricsAspect;
import kz.iitu.springlab.service.CatalogService;

@RestController
public class CatalogController {

    private final CatalogService catalogService;
    private final MetricsAspect metricsAspect;

    public CatalogController(CatalogService catalogService, MetricsAspect metricsAspect) {
        this.catalogService = catalogService;
        this.metricsAspect = metricsAspect;
    }

    @GetMapping("/api/lab4/item/{id}")
    public String item(@PathVariable long id) {
        return catalogService.findById(id);
    }

    @GetMapping("/api/lab4/items")
    public List<String> items(@RequestParam(defaultValue = "5") int limit) {
        return catalogService.findAll(limit);
    }

    @DeleteMapping("/api/lab4/item/{id}")
    public String remove(@PathVariable long id) {
        return catalogService.remove(id);
    }

    // Task 4.1
    @GetMapping("/api/lab4/proxy")
    public Map<String, String> proxyInfo() {
        return Map.of(
                "className", catalogService.getClass().getName(),
                "superClass", catalogService.getClass().getSuperclass().getSimpleName(),
                "isAopProxy", String.valueOf(AopUtils.isAopProxy(catalogService)),
                "isCglib", String.valueOf(AopUtils.isCglibProxy(catalogService)));
    }

    // Task 4.4 (before the fix)
    @GetMapping("/api/lab4/remove-twice/{id}")
    public String removeTwice(@PathVariable long id) {
        return catalogService.removeTwice(id);
    }

    // Task 4.5 (after the fix)
    @GetMapping("/api/lab4/remove-twice-fixed/{id}")
    public String removeTwiceFixed(@PathVariable long id) {
        return catalogService.removeTwiceFixed(id);
    }

    // Variant 11: summary of the @Measured metrics
    @GetMapping("/api/lab4/metrics")
    public Map<String, Map<String, Object>> metrics() {
        return metricsAspect.summary();
    }
}
