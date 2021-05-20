package org.bahmni.module.lisintegration.services;

import ca.uhn.hl7v2.AcknowledgmentCode;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.v25.message.ACK;
import ca.uhn.hl7v2.model.v25.message.ORR_O02;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.exception.LisException;
import org.bahmni.module.lisintegration.model.Lis;
import org.bahmni.module.lisintegration.model.OrderType;
import org.bahmni.module.lisintegration.repository.OrderTypeRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LisServiceTest {

    @Mock
    public OrderTypeRepository orderTypeRepository;

    @Mock
    public AbstractMessage requestMessage;

    @Spy
    @InjectMocks
    public LisService lisService = new LisService();
    private ORR_O02 orderResponse;
    private String orderTypeName;
    private OrderType orderType;
    private OpenMRSOrder openMRSOrder;

    @Before
    public void setup() {
        orderTypeName = "Radiology";
        orderType = new OrderType();
        orderType.setLis(new Lis());
        orderResponse = new ORR_O02();
        openMRSOrder = new OpenMRSOrder();
    }

    @Test
    public void shouldSendMessageSuccessfullyToLis() throws LLPException, IOException, HL7Exception {
        orderResponse.getMSA().getAcknowledgmentCode().setValue(AcknowledgmentCode.AA.toString());
        when(orderTypeRepository.getByName(orderTypeName)).thenReturn(orderType);
        doReturn(orderResponse).when(lisService).post(orderType.getLis(), requestMessage);
        doReturn("orderResponseString").when(lisService).parseResponse(orderResponse);

        try {
            String responseString = lisService.sendMessage(requestMessage, orderTypeName, openMRSOrder);
            assertEquals("orderResponseString", responseString);
        } catch (Exception e) {
            Assert.fail("Should not throw exception");
        }
    }

    @Test
    public void shouldAcceptACKMessageTypeAsResponseFromLis() throws HL7Exception, LLPException, IOException {
        ACK ack = new ACK();
        ack.getMSA().getAcknowledgmentCode().setValue(AcknowledgmentCode.AA.toString());
        when(orderTypeRepository.getByName(orderTypeName)).thenReturn(orderType);
        doReturn(ack).when(lisService).post(orderType.getLis(), requestMessage);
        doReturn("orderResponseString").when(lisService).parseResponse(ack);

        try {
            String responseString = lisService.sendMessage(requestMessage, orderTypeName, openMRSOrder);
            assertEquals("orderResponseString", responseString);
        } catch (Exception e) {
            Assert.fail("Should not throw exception");
        }
    }

    @Test(expected = LisException.class)
    public void shouldThrowExceptionIfTheLisRejectsTheMessage() throws HL7Exception, LLPException, IOException {
        orderResponse.getMSA().getAcknowledgmentCode().setValue(AcknowledgmentCode.AR.toString());
        when(orderTypeRepository.getByName(orderTypeName)).thenReturn(orderType);
        doReturn(orderResponse).when(lisService).post(orderType.getLis(), requestMessage);
        doReturn("Failure").when(lisService).parseResponse(orderResponse);

        lisService.sendMessage(requestMessage, orderTypeName, openMRSOrder);
    }

}