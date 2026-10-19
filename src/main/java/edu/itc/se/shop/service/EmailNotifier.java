package edu.itc.se.shop.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!dev")
@Primary // Default choice whenever a Notifier is requested without @Qualifier
public class EmailNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(EmailNotifier.class);

    @PostConstruct
    public void init() {
        log.info("EmailNotifier ready");
    }

    @Override
    public void send(String to, String text) {
        log.info("EMAIL to={} text={}", to, text);
    }

    @PreDestroy
    public void cleanup() {
        log.info("EmailNotifier closing");
    }
}
