package edu.byui.apj.storefront.jms.consumer;

import edu.byui.apj.storefront.model.CardOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class OrderConfirmationConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConfirmationConsumer.class);
    private final WebClient webClient;

    public OrderConfirmationConsumer(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8083").build();
    }

    @JmsListener(destination = "orderQueue")
    public void receiveOrderConfirmation(Long orderId) {
        log.info("Received order ID: {} from orderQueue", orderId);

        try {
            CardOrder order = webClient.get()
                    .uri("/order/{orderId}", orderId)
                    .retrieve()
                    .bodyToMono(CardOrder.class)
                    .block();

            if (order != null) {
                log.info("Order confirmation processed - Order details: ID={}, Customer={} {}, Total=${}, Items in cart={}",
                        order.getId(),
                        order.getCustomer() != null ? order.getCustomer().getFirstName() : "N/A",
                        order.getCustomer() != null ? order.getCustomer().getLastName() : "N/A",
                        order.getTotal(),
                        order.getCart() != null && order.getCart().getItems() != null ? order.getCart().getItems().size() : 0);
            } else {
                log.error("Failed to retrieve order information for order ID: {}", orderId);
            }
        } catch (WebClientResponseException e) {
            log.error("Error retrieving order {}: HTTP {} - {}", orderId, e.getStatusCode(), e.getStatusText());
        } catch (Exception e) {
            log.error("Error processing order confirmation for order ID: {}", orderId, e);
        }
    }
}