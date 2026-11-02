package edu.itc.se.shop;

import edu.itc.se.shop.service.Notifier;
import edu.itc.se.shop.service.ReportBuilder;
import edu.itc.se.shop.service.WelcomeService;
import java.time.Clock;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShopApplication {

    private static final Logger log = LoggerFactory.getLogger(ShopApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }

    // 1. Clock bean required by WelcomeService
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public CommandLineRunner runner(ApplicationContext ctx,
                                     ObjectProvider<WelcomeService> welcomeServiceProvider,
                                     ObjectProvider<ReportBuilder> reportBuilderProvider,
                                     ObjectProvider<edu.itc.se.shop.repository.ProductRepository> productRepoProvider) {
        return args -> {
            WelcomeService welcomeService = welcomeServiceProvider.getIfAvailable();
            if (welcomeService == null) {
                return;
            }
            // 1. Inspect loaded beans
            int beanCount = ctx.getBeanDefinitionCount();
            String[] notifierBeans = ctx.getBeanNamesForType(Notifier.class);
            log.info("{} beans; notifiers = {}", beanCount, Arrays.toString(notifierBeans));

            // 2. Test WelcomeService
            welcomeService.welcome("ann", false); // Default (Email)
            welcomeService.welcome("bob", true);  // Urgent (SMS via @Qualifier)

            // 3. Prove Prototype Scope
            ReportBuilder r1 = reportBuilderProvider.getObject();
            ReportBuilder r2 = reportBuilderProvider.getObject();
            boolean instancesDiffer = System.identityHashCode(r1) != System.identityHashCode(r2);
            log.info("prototype instances differ: {}", instancesDiffer);

            // 4. Test Database Connection & List Seeded Products
            edu.itc.se.shop.repository.ProductRepository productRepo = productRepoProvider.getIfAvailable();
            if (productRepo != null) {
                var products = productRepo.findAll();
                log.info("=== DATABASE CONNECTED SUCCESSFULLY ===");
                log.info("Found {} products in H2 database:", products.size());
                products.forEach(p -> log.info("  * [ID: {}] {} - ${} (Stock: {})", p.id(), p.name(), p.price(), p.stock()));
                log.info("========================================");
            }
        };
    }
}