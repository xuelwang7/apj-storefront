package edu.byui.apj.storefront.jms.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmationProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderConfirmationProducer.class);

    private final JmsTemplate jmsTemplate;

    @Autowired
    public OrderConfirmationProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendOrderConfirmation(Long orderId) {
        log.info("Sending order confirmation message for order ID: {}", orderId);
        jmsTemplate.convertAndSend("orderQueue", orderId);
        log.info("Order confirmation message sent successfully for order ID: {}", orderId);
    }
}