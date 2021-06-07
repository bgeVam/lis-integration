package org.bahmni.module.lisintegration.atomfeed.mappers;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSOrder;
import org.bahmni.module.lisintegration.atomfeed.contract.encounter.OpenMRSVisit;
import org.bahmni.module.lisintegration.services.OpenMRSService;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class OpenMRSVisitMapper {

    private ObjectMapper objectMapper;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public OpenMRSVisitMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    /**
     * This method is called to map the visitJson response to a {@link OpenMRSVisit}
     * object.
     * @param visitJSON is the json API response we get from the API.
     * @return visit returns a constructed {@link OpenMRSVisit} with the desired
     *         data from the input paramater.
     * @throws IOException if the String cannot be read via {@link #readTree(visitJSON)} method.
     * @throws ParseException if the date can not be parsed via {@link #parse(Date)} method.
     */
    public OpenMRSVisit map(String visitJSON) throws IOException, ParseException {
        OpenMRSVisit visit = new OpenMRSVisit();
        JsonNode jsonNode = objectMapper.readTree(visitJSON);

        visit.setVisitNumber(jsonNode.path("uuid").asText());
        visit.setAdmissionDate(dateFormat.parse(jsonNode.path("startDatetime").asText()));

        JsonNode encounters = jsonNode.path("encounters");
        if (encounters.size() > 0) {
            JsonNode orders = encounters.get(0).path("orders");
            if (orders.size() > 0) {
                JsonNode latestOrder = orders.get(orders.size() - 1);
                visit.setLatestOrderUuid(latestOrder.path("uuid").asText());
                OpenMRSOrder order = new OpenMRSService().getOrder(visit.getLatestOrderUuid());
                visit.setOrder(order);
            }
        }
        return visit;
    }
}
