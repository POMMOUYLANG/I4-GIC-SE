package edu.itc.se.shop.web;

import edu.itc.se.shop.config.ShopProperties;
import edu.itc.se.shop.service.ProductService;
import edu.itc.se.shop.web.dto.CreateProduct;
import edu.itc.se.shop.web.dto.ProductViewModel;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller serving server-rendered HTML via JSP.
 * Notice: Annotated with @Controller (renders views), NOT @RestController!
 */
@Controller
@RequestMapping("/products")
public class ProductPageController {

    private static final Logger log = LoggerFactory.getLogger(ProductPageController.class);

    private final ProductService service;
    private final ShopProperties properties;

    public ProductPageController(ProductService service, ShopProperties properties) {
        this.service = service;
        this.properties = properties;
    }

    // 1. GET /products (List + Search)
    @GetMapping
    public String listProducts(@RequestParam(required = false) String q, Model model) {
        List<ProductViewModel> viewModels = service.search(q).stream()
                .map(dto -> ProductViewModel.of(dto, properties.taxRate()))
                .toList();

        model.addAttribute("products", viewModels);
        model.addAttribute("currency", properties.currency());
        model.addAttribute("threshold", properties.lowStockThreshold());
        model.addAttribute("q", q);
        return "products/list"; // Resolves to /WEB-INF/jsp/products/list.jsp
    }

    // 2. GET /products/new (Show creation form)
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("product")) {
            model.addAttribute("product", new CreateProduct("", null, 0));
        }
        return "products/form"; // Resolves to /WEB-INF/jsp/products/form.jsp
    }

    // 3. POST /products (Submit form with POST-Redirect-GET)
    @PostMapping
    public String createProduct(@Valid @ModelAttribute("product") CreateProduct form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed when creating product: {}", bindingResult.getAllErrors());
            return "products/form"; // Re-display form with error messages
        }

        service.create(form);
        log.info("Successfully created product: {}", form.name());

        // Flash message survives the HTTP redirect!
        redirectAttributes.addFlashAttribute("message", "Product '" + form.name() + "' created successfully!");
        return "redirect:/products"; // POST-Redirect-GET pattern prevents duplicate form submission
    }
}
