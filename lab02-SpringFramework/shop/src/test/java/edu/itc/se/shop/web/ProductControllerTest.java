package edu.itc.se.shop.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.itc.se.shop.service.InsufficientStockException;
import edu.itc.se.shop.service.ProductNotFoundException;
import edu.itc.se.shop.service.ProductService;
import edu.itc.se.shop.web.dto.CreateProduct;
import edu.itc.se.shop.web.dto.ProductDto;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @WebMvcTest slices only the Web presentation layer.
 * Fast execution: does not spin up the database or embedded Tomcat!
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    MockMvc mvc;

    // Use @MockitoBean (Spring Boot 3.4+ / 4.x) or @MockBean (Spring Boot 3.0-3.3)
    @MockitoBean
    ProductService service;

    @Test
    void listReturns200WithProducts() throws Exception {
        given(service.search(null)).willReturn(List.of(
                new ProductDto(1L, "Laptop", new BigDecimal("1000.00"), 5)
        ));

        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void getByIdReturns404WhenProductDoesNotExist() throws Exception {
        given(service.getById(999L)).willThrow(new ProductNotFoundException(999L));

        mvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Product 999 not found"));
    }

    @Test
    void createWithInvalidBodyReturns400WithFieldErrors() throws Exception {
        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"name": "", "price": 0, "stock": -1}
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errors.length()").value(3));
    }

    @Test
    void createWithValidBodyReturns201WithLocationHeader() throws Exception {
        ProductDto created = new ProductDto(6L, "USB-C cable", new BigDecimal("7.90"), 25);
        given(service.create(any(CreateProduct.class))).willReturn(created);

        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"name": "USB-C cable", "price": 7.90, "stock": 25}
                            """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/products/6"))
                .andExpect(jsonPath("$.id").value(6))
                .andExpect(jsonPath("$.name").value("USB-C cable"));
    }

    @Test
    void patchStockReturns409WhenStockInsufficient() throws Exception {
        given(service.updateProductStock(6L, -30))
                .willThrow(new InsufficientStockException(6L, 25, -30));

        mvc.perform(patch("/api/products/6/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"delta": -30}
                            """))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").value("Product 6 has 25 units, cannot remove 30"));
    }
}
