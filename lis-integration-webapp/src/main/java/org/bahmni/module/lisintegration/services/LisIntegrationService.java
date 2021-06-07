package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.AbstractMessage;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Diagnosis;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSConcept;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSVisit;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Sample;
import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.module.lisintegration.atomfeed.mappers.OpenMRSEncounterToOrderMapper;
import org.bahmni.module.lisintegration.model.Order;
import org.bahmni.module.lisintegration.model.OrderDetails;
import org.bahmni.module.lisintegration.model.OrderType;
import org.bahmni.module.lisintegration.repository.OrderDetailsRepository;
import org.bahmni.module.lisintegration.repository.OrderRepository;
import org.bahmni.module.lisintegration.repository.OrderTypeRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@Component
public class LisIntegrationService {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(LisIntegrationService.class);

    @Value("${green.letters}")
    private String printGreen;

    @Value("${red.letters}")
    private String printRed;

    @Value("${default.letters}")
    private String printDefault;

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

    private LisService lisService;

    @Autowired
    public final void setLisService(LisService lisService) {
        this.lisService = lisService;
        try {
            LOG.info(printGreen + " Server is starting..." + printDefault);
            lisService.startServer();
            LOG.info(printGreen + "Server has been started..." + printDefault);
        } catch (Exception e) {
            LOG.error(printRed + "An error has occurred..." + printDefault);
        }
    }

    /**
     * processes the encounter
     *
     * @param openMRSEncounter is the object of {@link openMRSEncounter)
     * @throws IOException    if the patient cannot be fetched via
     *                        {@link #getPatient(patientUuid)} method
     * @throws IOException    if the sample cannot be fetched via
     *                        {@link #getSample(conceptUuid)} method
     * @throws IOException    if the message cannot be posted via
     *                        {@link #sendMessage(message, orderType, openMRSOrder)}
     *                        method
     * @throws ParseException if the patient cannot be fetched via
     *                        {@link #getPatient(patientUuid)} method
     * @throws HL7Exception   if the message cannot be created via
     *                        {@link #createMessage(openMRSOrder, sample, openMRSPatient, providers)}
     *                        method
     * @throws HL7Exception   if the message cannot be sent via
     *                        {@link #sendMessage(message, orderType, openMRSOrder)}
     *                        method
     * @throws LLPException   if the message cannot be sent via
     *                        {@link #sendMessage(message, orderType, openMRSOrder)}
     *                        method
     */
    public void processEncounter(OpenMRSEncounter openMRSEncounter)
            throws IOException, ParseException, HL7Exception, LLPException {
        OpenMRSPatient patient = openMRSService.getPatient(openMRSEncounter.getPatientUuid());
        OpenMRSVisit visit = (openMRSEncounter.getVisit() == null) ? null
            : openMRSService.getVisit(openMRSEncounter.getVisit().getUuid());
        List<OrderType> acceptableOrderTypes = orderTypeRepository.findAll();

        List<OpenMRSOrder> newAcceptableTestOrders = openMRSEncounter.getAcceptableTestOrders(acceptableOrderTypes);
        Collections.reverse(newAcceptableTestOrders);
        for (OpenMRSOrder openMRSOrder : newAcceptableTestOrders) {
            if (orderRepository.findByPlacerOrderUuid(openMRSOrder.getUuid()) == null) {
                OpenMRSConcept openMRSConcept = openMRSOrder.getConcept();
                Sample sample = openMRSService.getSample(openMRSConcept.getUuid());
                List<Diagnosis> diagnosis = openMRSService.getDiagnosis(openMRSEncounter.getEncounterUuid());
                AbstractMessage request = hl7Service.createMessage(openMRSOrder, diagnosis, sample, patient,

                        visit, openMRSEncounter.getProviders());
                String response = lisService.sendMessage(request, openMRSOrder.getOrderType(), openMRSOrder);
                Order order = openMRSEncounterToOrderMapper.map(openMRSOrder, openMRSEncounter, sample,
                        acceptableOrderTypes);

                orderRepository.save(order);
                orderDetailsRepository.save(new OrderDetails(order, request.encode(), response));
            }
        }
    }

}
