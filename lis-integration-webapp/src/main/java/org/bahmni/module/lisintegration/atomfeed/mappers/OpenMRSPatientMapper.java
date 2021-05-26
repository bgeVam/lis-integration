package org.bahmni.module.lisintegration.atomfeed.mappers;

import org.bahmni.module.lisintegration.atomfeed.contract.patient.OpenMRSPatient;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class OpenMRSPatientMapper {
    private ObjectMapper objectMapper;
    private SimpleDateFormat dateOfBirthFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public OpenMRSPatientMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    /**
     * maps the patient details of the PID segment
     *
     * @param patientJSON represents the data of the patient
     * @return patient returns the mapped patient details
     * @throws IOException if the jsonNode cannot be parsed correctly
     * @throws ParseException if birthday cannot be parset correctly
     */
    public OpenMRSPatient map(String patientJSON) throws IOException, ParseException {
        OpenMRSPatient patient = new OpenMRSPatient();
        JsonNode jsonNode = objectMapper.readTree(patientJSON);
        JsonNode identifierList = jsonNode.path("identifiers");

        patient.setPatientId(jsonNode.path("identifiers").get(0).path("identifier").asText());
        patient.setGivenName(jsonNode.path("person").path("preferredName").path("givenName").asText()
                .replaceAll("[\\W&&[^-]]", " "));
        patient.setFamilyName(jsonNode.path("person").path("preferredName").path("familyName").asText()
                .replaceAll("[\\W&&[^-]]", " "));
        patient.setMiddleName(jsonNode.path("person").path("preferredName").path("middleName").asText()
                .replaceAll("[\\W&&[^-]]", " "));
        patient.setGender(jsonNode.path("person").path("gender").asText());
        patient.setBirthDate(dateOfBirthFormat.parse(jsonNode.path("person").path("birthdate").asText()));
        for (JsonNode identifier : identifierList) {
            String identifierType = identifier.path("identifierType").path("display").asText();
            if (identifierType.equals("Social Security Number")) {
                patient.setSSNNumber(identifier.path("identifier").asText());
            } else if (identifierType.equals("Driving License")) {
                patient.setDrivingLicenseNumber(identifier.path("identifier").asText());
            }
        }

        return patient;
    }

}
