package edu.itc.shop.domain;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryProductRepository implements ProductRepository {

    private final ConcurrentMap<Long, Product> storage = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(0);

    public InMemoryProductRepository() {
        save(new Product(0, "Kettle", "KT-01", new BigDecimal("24.90"), 12, 0));
        save(new Product(0, "Mug", "MG-01", new BigDecimal("6.50"), 40, 0));
        save(new Product(0, "Tea", "TE-01", new BigDecimal("4.20"), 0, 0));
    }

    @Override
    public List<Product> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparingLong(Product::id))
                .toList();
    }

    @Override
    public Optional<Product> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return storage.values().stream()
                .filter(p -> p.sku().equalsIgnoreCase(sku))
                .findFirst();
    }

    @Override
    public Product save(Product product) {
        long id = product.id() == 0 ? ids.incrementAndGet() : product.id();
        Product saved = new Product(
                id,
                product.name(),
                product.sku(),
                product.price(),
                product.stock(),
                product.version() + 1
        );
        storage.put(id, saved);
        return saved;
    }

    @Override
    public boolean delete(long id) {
        return storage.remove(id) != null;
    }
}
