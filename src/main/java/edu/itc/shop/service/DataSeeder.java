package edu.itc.shop.service;

import edu.itc.shop.domain.*;
import edu.itc.shop.repository.CustomerRepository;
import edu.itc.shop.repository.OrderRepository;
import edu.itc.shop.repository.ProductRepository;
import edu.itc.shop.repository.TagRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
public class DataSeeder implements ApplicationRunner {

    private final CustomerRepository customers;
    private final ProductRepository products;
    private final OrderRepository orders;
    private final TagRepository tags;

    public DataSeeder(CustomerRepository customers, ProductRepository products, OrderRepository orders, TagRepository tags) {
        this.customers = customers;
        this.products = products;
        this.orders = orders;
        this.tags = tags;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (customers.count() > 0) return;

        // 3 Customers
        Customer ada = customers.save(new Customer("Ada Lovelace", "ada@example.com",
            new Address("10 St James", "London", "SW1"), new Address("10 St James", "London", "SW1")));
        Customer grace = customers.save(new Customer("Grace Hopper", "grace@example.com",
            new Address("Arlington Blvd", "Arlington", "VA"), new Address("Arlington Blvd", "Arlington", "VA")));
        Customer alan = customers.save(new Customer("Alan Turing", "alan@example.com",
            new Address("Wilmslow Rd", "Manchester", "M14"), new Address("Wilmslow Rd", "Manchester", "M14")));

        // Tags
        Tag tagSale = tags.save(new Tag("sale"));
        Tag tagNew = tags.save(new Tag("new"));
        Tag tagPro = tags.save(new Tag("pro"));

        // 12 Products
        List<Product> prodList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Category cat = Category.values()[i % Category.values().length];
            Product p = new Product("SKU-" + i, "Product " + i, Money.eur(String.valueOf(10 + i * 5)), cat, 20 + i);
            if (i % 2 == 0) p.tag(tagSale);
            if (i % 3 == 0) p.tag(tagNew);
            if (i % 4 == 0) p.tag(tagPro);
            prodList.add(p);
        }
        products.saveAll(prodList);

        // 30 Orders with mixed statuses
        Customer[] customerArray = {ada, grace, alan};
        OrderStatus[] statuses = OrderStatus.values();
        for (int i = 0; i < 30; i++) {
            Order o = new Order(customerArray[i % 3]);
            o.setStatus(statuses[i % statuses.length]);
            o.addLine(prodList.get(i % prodList.size()), 1 + (i % 3));
            o.addLine(prodList.get((i + 1) % prodList.size()), 2);
            orders.save(o);
        }
    }
}
