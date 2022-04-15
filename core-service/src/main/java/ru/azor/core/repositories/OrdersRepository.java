package ru.azor.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.azor.api.enums.OrderStatus;
import ru.azor.core.entities.Order;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {
    @Query("select o from Order o where o.username = ?1")
    List<Order> findAllByUsername(String username);

    @Query("select count(o) from Order o where o.orderStatus = ?1 and o.id = ?2")
    Long isOrderStatusPresent(OrderStatus orderStatus, Long orderId);

    @Modifying
    @Query("update Order o set o.orderStatus = ?1 where o.id = ?2")
    void changeOrderStatus(OrderStatus orderStatus, Long orderId);
}
