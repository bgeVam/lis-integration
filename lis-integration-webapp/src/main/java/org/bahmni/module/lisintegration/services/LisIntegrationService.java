package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.AbstractMessage;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Sample;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.atomfeed.mappers.OpenMRSEncounterToOrderMapper;
import org.bahmni.module.lisintegration.model.Order;
import org.bahmni.module.lisintegration.model.OrderDetails;
import org.bahmni.module.lisintegration.model.OrderType;
import org.bahmni.module.lisintegration.repository.OrderDetailsRepository;
import org.bahmni.module.lisintegration.repository.OrderRepository;
import org.bahmni.module.lisintegration.repository.OrderTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@Component
public class LisIntegrationService {

    @Autowired
    private OpenMRSEncounterToOrderMapper openMRSEncounterToOrderMapper;

    @Autowired
    private OpenMRSService openMRSService;

    @Autowired
    private HL7Service hl7Service;

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private LisService lisService;

    public void processEncounter(OpenMRSEncounter openMRSEncounter) throws IOException, ParseException, HL7Exception, LLPException {
        OpenMRSPatient patient = openMRSService.getPatient(openMRSEncounter.getPatientUuid());
        List<OrderType> acceptableOrderTypes = orderTypeRepository.findAll();

        List<OpenMRSOrder> newAcceptableTestOrders = openMRSEncounter.getAcceptableTestOrders(acceptableOrderTypes);
        Collections.reverse(newAcceptableTestOrders);
        for(OpenMRSOrder openMRSOrder : newAcceptableTestOrders) {
            if(orderRepository.findByOrderUuid(openMRSOrder.getUuid()) == null) {
                OpenMRSConcept openMRSConcept = openMRSOrder.getConcept();
                Sample sample = openMRSService.getSample(openMRSConcept.getUuid());
                AbstractMessage request = hl7Service.createMessage(openMRSOrder, sample, patient, openMRSEncounter.getProviders());
                String response = lisService.sendMessage(request, openMRSOrder.getOrderType());
                Order order = openMRSEncounterToOrderMapper.map(openMRSOrder, openMRSEncounter, sample, acceptableOrderTypes);

                orderRepository.save(order);
                orderDetailsRepository.save(new OrderDetails(order, request.encode(),response));
            }
        }
    }

}
