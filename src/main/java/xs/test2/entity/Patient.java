package xs.test2.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private AustralianAddress australianAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatientStatus status;

    @OneToMany(mappedBy = "keepPatient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientMergeHistory> keepMergeHistories = new ArrayList<>();

    @OneToMany(mappedBy = "discardPatient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientMergeHistory> discardMergeHistories = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PatientIdentifier> identifiers = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public AustralianAddress getAustralianAddress() {
        return australianAddress;
    }

    public void setAustralianAddress(AustralianAddress australianAddress) {
        this.australianAddress = australianAddress;
    }

    public PatientStatus getStatus() {
        return status;
    }

    public void setStatus(PatientStatus status) {
        this.status = status;
    }

    public List<PatientMergeHistory> getKeepMergeHistories() {
        return keepMergeHistories;
    }

    public void setKeepMergeHistories(List<PatientMergeHistory> keepMergeHistories) {
        this.keepMergeHistories = keepMergeHistories;
    }

    public List<PatientMergeHistory> getDiscardMergeHistories() {
        return discardMergeHistories;
    }

    public void setDiscardMergeHistories(List<PatientMergeHistory> discardMergeHistories) {
        this.discardMergeHistories = discardMergeHistories;
    }

    public List<PatientIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<PatientIdentifier> identifiers) {
        this.identifiers = identifiers;
    }
}
