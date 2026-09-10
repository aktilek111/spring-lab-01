package kz.iitu.springlab.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api")
public class HelloController {

    @Value("${app.owner:unknown}")
    private String owner;

    // Возвращает приветствие с именем
    @GetMapping("/hello")
    public Greeting hello(@RequestParam(defaultValue = "world") String name) {
        return new Greeting("Hello, " + name + "!", owner, LocalDateTime.now());
    }

    // Возвращает информацию о сервере: владелец, версия Java, число ядер
    @GetMapping("/info")
    public Info info() {
        return new Info(owner, System.getProperty("java.version"), Runtime.getRuntime().availableProcessors());
    }

    // Индивидуальное задание, вариант 11: возвращает текущее время в заданном часовом поясе
    @GetMapping("/time")
    public ResponseEntity<?> time(@RequestParam(required = false) String zone) {
        String zoneName = (zone == null || zone.isBlank()) ? "Asia/Almaty" : zone;
        try {
            ZoneId zoneId = ZoneId.of(zoneName);
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            String offset = now.getOffset().getId();
            return ResponseEntity.ok(new TimeInfo(zoneName, now.toLocalDateTime().toString(), offset));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unknown time zone: " + zoneName);
        }
    }

    public record TimeInfo(String zone, String currentTime, String utcOffset) { }
    public record Greeting(String message, String owner, LocalDateTime timestamp) { }
    public record Info(String owner, String javaVersion, int cpuCores) { }
}