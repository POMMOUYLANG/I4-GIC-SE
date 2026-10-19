package edu.itc.se.shop.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import edu.itc.se.shop.config.ShopProperties;
import edu.itc.se.shop.service.ProductService;
import edu.itc.se.shop.web.dto.CreateProduct;
import edu.itc.se.shop.web.dto.ProductDto;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductPageController.class)
@EnableConfigurationProperties(ShopProperties.class)
class ProductPageControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ProductService service;

    @Test
    void listProductsReturnsViewWithProducts() throws Exception {
        given(service.search(null)).willReturn(List.of(
                new ProductDto(1L, "Laptop", new BigDecimal("1000.00"), 5)
        ));

        mvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products", "currency", "threshold"));
    }

    @Test
    void showCreateFormReturnsFormView() throws Exception {
        mvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void createProductWithValidationErrorsReturnsFormView() throws Exception {
        mvc.perform(post("/products")
                        .param("name", "")
                        .param("price", "0.00")
                        .param("stock", "-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().hasErrors());
    }

    @Test
    void createProductSuccessRedirectsAndAddsFlashMessage() throws Exception {
        ProductDto created = new ProductDto(10L, "New Gadget", new BigDecimal("19.99"), 50);
        given(service.create(any(CreateProduct.class))).willReturn(created);

        mvc.perform(post("/products")
                        .param("name", "New Gadget")
                        .param("price", "19.99")
                        .param("stock", "50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attribute("message", "Product 'New Gadget' created successfully!"));
    }
}
