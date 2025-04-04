package edu.byui.apj.storefront.jms.producer;

import edu.byui.apj.storefront.model.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class CartCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(CartCleanupJob.class);
    private final WebClient webClient;

    public CartCleanupJob(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8083").build();
    }

    public List<Cart> getCartsWithoutOrders() {
        log.info("Fetching carts without orders");
        try {
            Cart[] carts = webClient.get()
                    .uri("/cart/noorder")
                    .retrieve()
                    .bodyToMono(Cart[].class)
                    .block();

            if (carts != null) {
                log.info("Found {} carts without orders", carts.length);
                return Arrays.asList(carts);
            } else {
                log.warn("No carts returned from service");
                return List.of();
            }
        } catch (Exception e) {
            log.error("Error fetching carts without orders", e);
            throw new RuntimeException("Failed to fetch carts without orders", e);
        }
    }

    public void cleanupCart(String cartId) {
        log.info("Cleaning up cart with ID: {}", cartId);
        try {
            webClient.delete()
                    .uri("/cart/{cartId}", cartId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Successfully deleted cart with ID: {}", cartId);
        } catch (WebClientResponseException e) {
            log.error("Failed to delete cart with ID: {} - HTTP Status: {}", cartId, e.getStatusCode());
        } catch (Exception e) {
            log.error("Error deleting cart with ID: {}", cartId, e);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    public void cleanupCarts() {
        log.info("Starting cart cleanup job");

        try {
            List<Cart> cartsToCleanup = getCartsWithoutOrders();

            if (cartsToCleanup.isEmpty()) {
                log.info("No carts to clean up");
                return;
            }

            ExecutorService executorService = Executors.newFixedThreadPool(2);

            for (Cart cart : cartsToCleanup) {
                executorService.submit(() -> cleanupCart(cart.getId()));
            }

            executorService.shutdown();

            // Wait for all tasks to complete or timeout after 5 minutes
            if (executorService.awaitTermination(5, TimeUnit.MINUTES)) {
                log.info("Cart cleanup complete");
            } else {
                log.warn("Cart cleanup timed out before completion");
                executorService.shutdownNow();
            }
        } catch (Exception e) {
            log.error("Error during cart cleanup job", e);
        }
    }
}