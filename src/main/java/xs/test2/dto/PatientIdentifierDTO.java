package xs.test2.dto;

import xs.test2.entity.IdentifierType;

import java.util.UUID;

public class PatientIdentifierDTO {

    private UUID id;

    private IdentifierType idType;

    private String idValue;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public IdentifierType getIdType() {
        return idType;
    }

    public void setIdType(IdentifierType idType) {
        this.idType = idType;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }
}
