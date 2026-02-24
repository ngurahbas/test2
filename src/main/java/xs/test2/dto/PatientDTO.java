package xs.test2.dto;

import java.util.List;
import java.util.UUID;

public class PatientDTO extends NewPatientDTO {

    private UUID id;

    private List<PatientIdentifierDTO> identifiers;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<PatientIdentifierDTO> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<PatientIdentifierDTO> identifiers) {
        this.identifiers = identifiers;
    }
}
