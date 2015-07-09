package org.bahmni.module.pacsintegration.repository;


import org.bahmni.module.pacsintegration.model.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface OrderTypeRepository extends JpaRepository<OrderType, Integer> {

    OrderType getByName(String name);

}
