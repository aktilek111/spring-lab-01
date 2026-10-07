package kz.iitu.springlab.web;

import kz.iitu.springlab.notify.NotificationService;
import kz.iitu.springlab.lifecycle.LifecycleDemo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lab2")
public class Lab2Controller {

    private final NotificationService notifications;
    private final LifecycleDemo lifecycle;

    public Lab2Controller(NotificationService notifications, LifecycleDemo lifecycle) {
        this.notifications = notifications;
        this.lifecycle = lifecycle;
    }

    @GetMapping("/notify")
    public Map<String, Object> notify(@RequestParam(defaultValue = "Hello") String text) {
        return Map.of(
                "primary", notifications.viaPrimary(text),
                "console", notifications.viaConsole(text),
                "all", notifications.viaAll(text),
                "beanNames", notifications.names()
        );
    }

    @GetMapping("/lifecycle")
    public List<String> lifecycle() {
        return lifecycle.events();
    }

    @GetMapping("/custom")
    public String custom(@RequestParam(defaultValue = "titlecase") String channel,
                         @RequestParam(defaultValue = "Hello") String text) {
        return notifications.viaChannel(channel, text);
    }
}