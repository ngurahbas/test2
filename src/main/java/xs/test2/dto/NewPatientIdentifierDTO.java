package xs.test2.dto;

import xs.test2.shared.IdentifierType;

public class NewPatientIdentifierDTO {

    private IdentifierType idType;

    private String idValue;

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
