package xs.test2.dto;

import java.util.UUID;

public class PatientIdentifierDTO extends NewPatientIdentifierDTO {

    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
