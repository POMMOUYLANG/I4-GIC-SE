package edu.itc.se.shop.web;

import edu.itc.se.shop.service.ProductService;
import edu.itc.se.shop.web.dto.CreateProduct;
import edu.itc.se.shop.web.dto.ProductDto;
import edu.itc.se.shop.web.dto.UpdateStockRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // 1. GET /api/products?q=filter
    @GetMapping
    public List<ProductDto> list(@RequestParam(required = false) String q) {
        return service.search(q);
    }

    // 2. GET /api/products/{id} -> 200 or 404
    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable long id) {
        return service.getById(id);
    }

    // 3. POST /api/products -> 201 with Location: /api/products/{id}
    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody @Valid CreateProduct cmd) {
        ProductDto created = service.create(cmd);
        URI location = URI.create("/api/products/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    // 4. PATCH /api/products/{id}/stock -> 200 with updated body, 409 if negative
    @PatchMapping("/{id}/stock")
    public ProductDto updateStock(@PathVariable long id, @RequestBody UpdateStockRequest req) {
        return service.updateProductStock(id, req.delta());
    }

    // 5. DELETE /api/products/{id} -> 204 No Content, 404 if missing
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
