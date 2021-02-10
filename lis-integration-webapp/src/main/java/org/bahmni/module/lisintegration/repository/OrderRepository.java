package org.bahmni.module.lisintegration.repository;

import org.bahmni.module.lisintegration.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByOrderUuid(String orderUuid);
}
