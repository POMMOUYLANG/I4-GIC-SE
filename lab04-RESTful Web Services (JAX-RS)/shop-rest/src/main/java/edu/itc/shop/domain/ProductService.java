package edu.itc.shop.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

public class ProductService {

    private static final Map<String, Comparator<Product>> SORT_FIELDS = Map.of(
            "name", Comparator.comparing(Product::name, String.CASE_INSENSITIVE_ORDER),
            "price", Comparator.comparing(Product::price),
            "stock", Comparator.comparingInt(Product::stock)
    );

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> list() {
        return repo.findAll();
    }

    public List<Product> search(String q, Money minPrice, Money maxPrice, boolean inStock, String sortField, boolean descending) {
        Comparator<Product> comparator = SORT_FIELDS.get(sortField);
        if (comparator == null) {
            throw new BadRequestException("Unknown sort field " + sortField);
        }
        if (descending) {
            comparator = comparator.reversed();
        }
        validateEur(minPrice, "minPrice");
        validateEur(maxPrice, "maxPrice");

        String needle = (q == null || q.isBlank()) ? null : q.trim().toLowerCase();
        return repo.findAll().stream()
                .filter(p -> needle == null || p.name().toLowerCase().contains(needle))
                .filter(p -> !inStock || p.stock() > 0)
                .filter(p -> minPrice == null || p.price().compareTo(minPrice.amount()) >= 0)
                .filter(p -> maxPrice == null || p.price().compareTo(maxPrice.amount()) <= 0)
                .sorted(comparator)
                .toList();
    }

    private static void validateEur(Money money, String paramName) {
        if (money != null && !"EUR".equalsIgnoreCase(money.currency())) {
            throw new BadRequestException(paramName + " must use EUR currency");
        }
    }

    public Product get(long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }

    public synchronized Product create(Product draft) {
        repo.findBySku(draft.sku()).ifPresent(p -> {
            throw new DuplicateSkuException(draft.sku());
        });
        return repo.save(new Product(0, draft.name(), draft.sku(), draft.price(), draft.stock(), 0));
    }

    public synchronized Product update(long id, Product replacement) {
        Product existing = get(id);
        repo.findBySku(replacement.sku())
                .filter(other -> other.id() != id)
                .ifPresent(other -> {
                    throw new DuplicateSkuException(replacement.sku());
                });
        return repo.save(new Product(
                id,
                replacement.name(),
                replacement.sku(),
                replacement.price(),
                replacement.stock(),
                existing.version()
        ));
    }

    public void delete(long id) {
        if (!repo.delete(id)) {
            throw new NotFoundException("Product " + id + " not found");
        }
    }

    public synchronized Product adjustStock(long id, int delta) {
        Product existing = get(id);
        int updatedStock = existing.stock() + delta;
        if (updatedStock < 0) {
            throw new InsufficientStockException(id, existing.stock(), delta);
        }
        return repo.save(new Product(
                existing.id(),
                existing.name(),
                existing.sku(),
                existing.price(),
                updatedStock,
                existing.version()
        ));
    }
}
