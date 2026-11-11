package edu.itc.shop.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, String currency) {

    public Money {
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(currency, "currency must not be null");
    }

    public static Money parse(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Money string must not be blank");
        }
        String[] parts = raw.trim().split("\\s+");
        if (parts.length != 2 || !parts[1].matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Cannot parse Money from '" + raw + "'");
        }
        try {
            BigDecimal amount = new BigDecimal(parts[0]);
            return new Money(amount, parts[1].toUpperCase());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot parse Money from '" + raw + "'", e);
        }
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }
}
