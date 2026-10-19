package edu.itc.se.shop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Pure JUnit 5 Unit Test — NO Spring Context required!
 * Runs in under 10 milliseconds.
 */
class WelcomeServiceTest {

    // Test Double (Fake Notifier) to capture sent notifications
    static class FakeNotifier implements Notifier {
        final List<String> sentTo = new ArrayList<>();
        final List<String> sentText = new ArrayList<>();

        @Override
        public void send(String to, String text) {
            sentTo.add(to);
            sentText.add(text);
        }
    }

    @Test
    void shouldFormatWelcomeMessageAndSendViaChosenNotifier() {
        // Arrange: Freeze time at 09:15:02 UTC
        Clock fixedClock = Clock.fixed(Instant.parse("2026-09-25T09:15:02Z"), ZoneId.of("UTC"));
        FakeNotifier defaultNotifier = new FakeNotifier();
        FakeNotifier urgentNotifier = new FakeNotifier();

        WelcomeService service = new WelcomeService(defaultNotifier, urgentNotifier, fixedClock);

        // Act: Test standard notification
        String result = service.welcome("Ann", false);

        // Assert
        assertEquals("Welcome Ann! It is 09:15:02", result);
        assertEquals(1, defaultNotifier.sentTo.size());
        assertEquals("Ann", defaultNotifier.sentTo.get(0));
        assertTrue(urgentNotifier.sentTo.isEmpty(), "Urgent notifier should not have been called");

        // Act: Test urgent notification
        service.welcome("Bob", true);
        assertEquals(1, urgentNotifier.sentTo.size());
        assertEquals("Bob", urgentNotifier.sentTo.get(0));
    }
}
