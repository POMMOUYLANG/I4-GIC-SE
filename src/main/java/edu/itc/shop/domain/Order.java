package edu.itc.shop.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
    @SequenceGenerator(name = "orders_seq", sequenceName = "orders_seq", allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.NEW;

    @Column(name = "placed_at", nullable = false)
    private Instant placedAt = Instant.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 16)
    private List<OrderLine> lines = new ArrayList<>();

    @Version
    private long version;

    protected Order() {}

    public Order(Customer customer) {
        this.customer = customer;
        this.placedAt = Instant.now();
        this.status = OrderStatus.NEW;
    }

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public OrderStatus getStatus() { return status; }
    public Instant getPlacedAt() { return placedAt; }
    public long getVersion() { return version; }

    public void setStatus(OrderStatus status) { this.status = status; }

    public OrderLine addLine(Product product, int quantity) {
        OrderLine line = new OrderLine(this, product, quantity, product.getPrice());
        this.lines.add(line);
        return line;
    }

    public void removeLine(OrderLine line) {
        this.lines.remove(line);
        line.setOrder(null);
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    public Money total() {
        if (lines.isEmpty()) {
            return Money.eur("0.00");
        }
        Money sum = Money.eur("0.00");
        for (OrderLine line : lines) {
            sum = sum.add(line.getUnitPrice().times(line.getQuantity()));
        }
        return sum;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Order other && id != null && id.equals(other.getId()));
    }

    @Override
    public int hashCode() {
        return Order.class.hashCode();
    }

    @Override
    public String toString() {
        return "Order[id=%d, status=%s, placedAt=%s]".formatted(id, status, placedAt);
    }
}
