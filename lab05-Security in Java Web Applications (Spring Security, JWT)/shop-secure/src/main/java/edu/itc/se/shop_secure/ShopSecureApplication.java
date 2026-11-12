package edu.itc.se.shop_secure;

import edu.itc.se.shop_secure.domain.Product;
import edu.itc.se.shop_secure.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class ShopSecureApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopSecureApplication.class, args);
	}

	@Bean
	CommandLineRunner seedProducts(ProductRepository productRepository) {
		return args -> {
			if (productRepository.count() == 0) {
				productRepository.save(new Product("USB-C cable", new BigDecimal("7.90"), 25));
				productRepository.save(new Product("Ergonomic Keyboard", new BigDecimal("89.00"), 12));
				productRepository.save(new Product("Wireless Mouse", new BigDecimal("29.50"), 40));
			}
		};
	}

}
