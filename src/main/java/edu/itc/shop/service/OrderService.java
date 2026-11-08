package edu.itc.shop.service;

import edu.itc.shop.domain.Order;
import edu.itc.shop.domain.OrderStatus;
import edu.itc.shop.repository.OrderRepository;
import edu.itc.shop.web.dto.OrderDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void markPaid(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.PAID);
    }

    @Transactional
    public void changeStatus(Long orderId, OrderStatus next) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(next);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> findOrdersByCustomerEmail(String email) {
        return orderRepository.findWithLinesByCustomerEmail(email).stream()
            .map(OrderDto::from)
            .toList();
    }
}
