package edu.byui.apj.storefront.jms.controller;

import edu.byui.apj.storefront.jms.producer.OrderConfirmationProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderConfirmationController {

    private static final Logger log = LoggerFactory.getLogger(OrderConfirmationController.class);
    private final OrderConfirmationProducer orderConfirmationProducer;

    @Autowired
    public OrderConfirmationController(OrderConfirmationProducer orderConfirmationProducer) {
        this.orderConfirmationProducer = orderConfirmationProducer;
    }

    @GetMapping("/confirm/{orderId}")
    public ResponseEntity<String> confirmOrder(@PathVariable Long orderId) {
        log.info("Received request to confirm order ID: {}", orderId);

        try {
            orderConfirmationProducer.sendOrderConfirmation(orderId);
            String message = String.format("Order confirm message sent for order ID: %d", orderId);
            log.info(message);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error("Error sending confirmation for order ID: {}", orderId, e);
            return ResponseEntity.internalServerError().body("Error processing order confirmation");
        }
    }
}