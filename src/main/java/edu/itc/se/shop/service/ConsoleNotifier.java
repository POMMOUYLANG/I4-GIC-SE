package edu.itc.se.shop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev") // Active ONLY when spring.profiles.active contains 'dev'
@Primary        // Competes with EmailNotifier when dev profile is active!
public class ConsoleNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(ConsoleNotifier.class);

    @Override
    public void send(String to, String text) {
        log.info("CONSOLE to={} text={}", to, text);
    }
}
