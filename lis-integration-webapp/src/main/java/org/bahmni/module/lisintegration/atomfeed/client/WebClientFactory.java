package org.bahmni.module.lisintegration.atomfeed.client;

import org.bahmni.webclients.ConnectionDetails;
import org.bahmni.webclients.HttpClient;
import org.bahmni.webclients.openmrs.OpenMRSLoginAuthenticator;

public final class WebClientFactory {

    private WebClientFactory() {
        throw new IllegalStateException("WebClientFactory class");
      }

    public static HttpClient getClient() {
        ConnectionDetails connectionDetails = org.bahmni.module.lisintegration.atomfeed.client.ConnectionDetails.get();
        return new HttpClient(connectionDetails, getAuthenticator(connectionDetails));
    }


    private static OpenMRSLoginAuthenticator getAuthenticator(ConnectionDetails connectionDetails) {
        return new OpenMRSLoginAuthenticator(connectionDetails);

    }
}
