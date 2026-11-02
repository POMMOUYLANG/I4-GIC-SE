package edu.itc.shop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public record Money(
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    BigDecimal amount,

    @Column(name = "currency", nullable = false, length = 3, columnDefinition = "CHAR(3)")
    String currency
) {
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
    }

    public static Money eur(String value) {
        return new Money(new BigDecimal(value), "EUR");
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public Money times(int qty) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(qty)), this.currency);
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies: " + this.currency + " vs " + other.currency);
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
}
