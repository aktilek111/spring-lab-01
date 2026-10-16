package kz.iitu.springlab.notify;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component("titlecase")
@Order(3)
public class TitleCaseNotifier implements Notifier {
    private static final Logger log = LoggerFactory.getLogger(TitleCaseNotifier.class);

    @PostConstruct
    void init() { log.info("TitleCaseNotifier initialized"); }

    @Override
    public String send(String message) {
        String[] words = message.split(" ");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (words[i].isEmpty()) continue;
            result.append(Character.toUpperCase(words[i].charAt(0)))
                    .append(words[i].substring(1).toLowerCase());
            if (i < words.length - 1) result.append(" ");
        }
        return result.toString();
    }

    @Override
    public String channel() { return "titlecase"; }
}