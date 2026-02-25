package xs.test2.entity;

import jakarta.persistence.*;
import xs.test2.shared.IdentifierType;

import java.util.UUID;

@Entity
@Table(name = "patient_identifier", indexes = {
        @Index(name = "idx_patient_id", columnList = "patient_id")
})
public class PatientIdentifier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "id_type", nullable = false)
    private IdentifierType idType;

    @Column(name = "id_value", nullable = false)
    private String idValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}