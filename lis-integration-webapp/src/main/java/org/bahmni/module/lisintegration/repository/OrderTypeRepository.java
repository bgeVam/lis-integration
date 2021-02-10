package org.bahmni.module.lisintegration.repository;


import org.bahmni.module.lisintegration.model.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderTypeRepository extends JpaRepository<OrderType, Integer> {

    OrderType getByName(String name);

}
