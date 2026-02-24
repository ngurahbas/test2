package xs.test2.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "patient_merge_history")
public class PatientMergeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keep_patient_id", nullable = false)
    private Patient keepPatient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discard_patient_id", nullable = false)
    private Patient discardPatient;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Patient getKeepPatient() {
        return keepPatient;
    }

    public void setKeepPatient(Patient keepPatient) {
        this.keepPatient = keepPatient;
    }

    public Patient getDiscardPatient() {
        return discardPatient;
    }

    public void setDiscardPatient(Patient discardPatient) {
        this.discardPatient = discardPatient;
    }
}
