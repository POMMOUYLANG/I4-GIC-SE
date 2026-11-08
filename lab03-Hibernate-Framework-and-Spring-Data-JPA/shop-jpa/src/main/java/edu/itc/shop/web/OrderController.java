package edu.itc.shop.web;

import edu.itc.shop.domain.OrderStatus;
import edu.itc.shop.repository.OrderRepository;
import edu.itc.shop.service.OrderService;
import edu.itc.shop.web.dto.OrderDto;
import edu.itc.shop.web.dto.OrderSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderController(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public Page<OrderSummary> list(
        @RequestParam OrderStatus status,
        @PageableDefault(size = 20, sort = "placedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return orderRepository.findByStatus(status, pageable);
    }

    @GetMapping("/by-customer/{email}")
    public List<OrderDto> listByCustomer(@PathVariable String email) {
        return orderService.findOrdersByCustomerEmail(email);
    }
}
