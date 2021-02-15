package org.bahmni.module.lisintegration.repository;

import org.bahmni.module.lisintegration.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {
}
