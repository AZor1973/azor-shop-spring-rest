package ru.azor.core.order_test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.azor.api.enums.OrderStatus;
import ru.azor.core.entities.Order;
import ru.azor.core.repositories.OrdersRepository;

import java.math.BigDecimal;
import java.util.List;

@DataJpaTest
public class OrderRepositoryTest {
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    TestEntityManager entityManager;
    private static final String USERNAME = "test_user";
    private static final String ADDRESS = "address";
    private static final String PHONE = "123456";
    private static final BigDecimal TOTAL_PRICE = BigDecimal.valueOf(100);

    @BeforeEach
    public void init() {
        Order order = new Order();
        order.setUsername(USERNAME);
        order.setAddress(ADDRESS);
        order.setPhone(PHONE);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setTotalPrice(TOTAL_PRICE);
        order.setItems(List.of());
        entityManager.persist(order);
        entityManager.flush();
    }

    @Test
    public void findAllByUsernameTest() {
        List<Order> orderList = ordersRepository.findAllByUsername(USERNAME);
        Assertions.assertEquals(2, orderList.size());
        Assertions.assertEquals(OrderStatus.CREATED, orderList.get(0).getOrderStatus());
        Assertions.assertEquals(USERNAME, orderList.get(0).getUsername());
        Assertions.assertEquals(USERNAME, orderList.get(1).getUsername());
    }

    @Test
    public void isOrderStatusPresentTest() {
        Assertions.assertTrue(ordersRepository.countByStatusAndId(OrderStatus.CREATED, 1L) > 0);
    }

    @Test
    public void changeOrderStatusTest() {
        Assertions.assertTrue(ordersRepository.countByStatusAndId(OrderStatus.CREATED, 1L) > 0);
        ordersRepository.changeOrderStatus(OrderStatus.PAID, 1L);
        Assertions.assertTrue(ordersRepository.countByStatusAndId(OrderStatus.PAID, 1L) > 0);
    }
}
