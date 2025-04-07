package edu.byui.apj.storefront.db.repository;

import edu.byui.apj.storefront.db.model.CardOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testSaveAndFindOrder() {
        CardOrder order = new CardOrder();
        CardOrder savedOrder = orderRepository.save(order);

        CardOrder found = orderRepository.findById(savedOrder.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(savedOrder.getId());
    }
}
