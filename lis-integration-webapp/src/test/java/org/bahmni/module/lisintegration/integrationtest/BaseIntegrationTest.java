package org.bahmni.module.lisintegration.integrationtest;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.message.ORM_O01;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import org.bahmni.module.LisIntegration;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LisIntegration.class)
public abstract class BaseIntegrationTest {

    public static HL7Service lisStubServer;
    private static final Logger LOG = LoggerFactory.getLogger(BaseIntegrationTest.class);

    @Before
    public void startLisStubServer() throws InterruptedException, UnknownHostException {
        HapiContext hapiContext = new DefaultHapiContext();
        int port = 1911;
        lisStubServer = hapiContext.newServer(port, false);
        lisStubServer.registerApplication("ORM", "O01", new ReceivingApplication() {
            @Override
            public Message processMessage(Message message, Map<String, Object> map)
                    throws ReceivingApplicationException, HL7Exception {
                ORM_O01 ormMessage = (ORM_O01) message;
                return HL7Utils.generateORRwithAccept(ormMessage.getMSH().getMessageControlID().getValue(),
                        "BahmniEMR");
            }

            @Override
            public boolean canProcess(Message message) {
                return true;
            }
        });
        lisStubServer.startAndWait();
        System.setProperty("ca.uhn.hl7v2.app.initiator.timeout", Integer.toString(2000));
        LOG.debug("Starting lis stub lisStubServer at " + Inet4Address.getLocalHost().getHostAddress() + ":" + port
                + " with timeout of " + 2000);
    }

    @After
    public void stopLisStubServer() {
        lisStubServer.stopAndWait();
    }
}
