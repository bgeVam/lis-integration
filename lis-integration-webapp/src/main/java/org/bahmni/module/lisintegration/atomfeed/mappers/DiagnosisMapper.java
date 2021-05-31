package org.bahmni.module.lisintegration.atomfeed.mappers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bahmni.module.lisintegration.atomfeed.contract.encounter.Diagnosis;
import org.bahmni.module.lisintegration.services.OpenMRSService;
import org.bahmni.webclients.ObjectMapperRepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DiagnosisMapper {
    private ObjectMapper objectMapper;

    @Value("${concept.code_diagnosis}")
    private String conceptCodeDiagnosis;

    @Value("${concept.diagnosis_order}")
    private String conceptDiagnosisOrder;

    public DiagnosisMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    /**
     * This method is called to map the encounter response to a {@link Diagnosis}
     * as list object.
     *
     * @param Diagnosis is the json API response we get from the API.
     * @return diagnosis returns a list of {@link Diagnosis} with the desired
     *         data from the input paramater.
     * @throws IOException
     * @throws ParseException
     */
    public List<Diagnosis> map(String encounterJSON) throws IOException, ParseException {
        OpenMRSService service = new OpenMRSService();
        JsonNode diagnosisCodeJson = objectMapper.readTree(encounterJSON);

        Integer lengthObs = diagnosisCodeJson.path("obs").size();
        List<Diagnosis> diagnosisList = new ArrayList<Diagnosis>(lengthObs);
        Diagnosis diagnosisObj = new Diagnosis();

        for (int observation = 0; observation < lengthObs; observation++) {
            JsonNode obsGroupMembers = diagnosisCodeJson.path("obs").get(observation).path("groupMembers");

            for (JsonNode diagnosis: obsGroupMembers) {
                String diagnosisConceptUuid = diagnosis.path("concept").path("uuid").asText();
                if (conceptCodeDiagnosis.equals(diagnosisConceptUuid)) {
                    String diagnosisUuid = diagnosis.path("value").path("uuid").asText();
                    Date dateDiagnosis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(
                                                            diagnosis.path("obsDatetime").asText());
                    String diagnosisConceptJSON = service.getConcept(diagnosisUuid);
                    JsonNode diagnosisConcpet = objectMapper.readTree(diagnosisConceptJSON);
                    String diagnosisReferenceTermUuid = diagnosisConcpet.path("mappings").get(0)
                                                                        .path("conceptReferenceTerm")
                                                                        .path("uuid").asText();
                    String diagnosisCode = service.getDiagnosisCode(diagnosisReferenceTermUuid);

                    diagnosisObj.setName(diagnosis.path("value").path("name").path("name").asText());
                    diagnosisObj.setCode(diagnosisCode);
                    diagnosisObj.setDate(dateDiagnosis);
                }
                if (conceptDiagnosisOrder.equals(diagnosisConceptUuid)) {
                    diagnosisObj.setType(diagnosis.path("value").path("name").path("name").asText());
                }
                if (diagnosisObj.getName() != null && diagnosisObj.getCode() != null
                     && diagnosisObj.getType() != null) {
                    diagnosisList.add(diagnosisObj);
                    diagnosisObj = new Diagnosis();
                }
            }
        }
        return diagnosisList;
    }
}
