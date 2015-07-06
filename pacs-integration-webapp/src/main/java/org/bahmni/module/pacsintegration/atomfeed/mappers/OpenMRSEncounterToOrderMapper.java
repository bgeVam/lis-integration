package org.bahmni.module.pacsintegration.atomfeed.mappers;

import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.pacsintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.pacsintegration.model.OrderType;
import org.bahmni.module.pacsintegration.model.Order;
import org.bahmni.module.pacsintegration.repository.OrderRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class OpenMRSEncounterToOrderMapper {
    public Collection<Order> map(OpenMRSEncounter openMRSEncounter, List<OrderType> acceptableOrderTypes, OrderRepository orderRepository) {
        Collection<Order> orders = new ArrayList<Order>();
        String providerName = getProviderName(openMRSEncounter);
        for (OpenMRSOrder openMRSOrder : openMRSEncounter.getTestOrders()) {
            OrderType orderType = findOrderType(acceptableOrderTypes, openMRSOrder.getOrderType());
            Order existingOrder = orderRepository.findByOrderUuid(openMRSOrder.getUuid());
            if (orderType != null && existingOrder == null && !openMRSOrder.isVoided()) {
                Order order = new Order();
                order.setOrderUuid(openMRSOrder.getUuid());
                order.setTestName(openMRSOrder.getConcept().getName().getName());
                order.setTestUuid(openMRSOrder.getConcept().getUuid());
                order.setOrderType(orderType);
                order.setDateCreated(new Date());
                order.setCreator(providerName);
                orders.add(order);
            }
        }
        return orders;
    }

    private String getProviderName(OpenMRSEncounter openMRSEncounter) {
        return openMRSEncounter.getProviders().size() > 0 ? openMRSEncounter.getProviders().get(0).getName() != null ? openMRSEncounter.getProviders().get(0).getName() : openMRSEncounter.getProviders().get(0).getUuid() : null;
    }

    private OrderType findOrderType(List<OrderType> acceptableOrderTypes, String orderType) {
        for (OrderType acceptableOrderType : acceptableOrderTypes) {
            if (acceptableOrderType.getName().equals(orderType)) {
                return acceptableOrderType;
            }
        }
        return null;
    }
}
