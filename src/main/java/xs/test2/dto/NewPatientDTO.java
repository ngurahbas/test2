package xs.test2.dto;

import xs.test2.entity.AustralianAddress;
import xs.test2.entity.Gender;

import java.time.LocalDate;

public class NewPatientDTO {

    private String firstName;

    private String lastName;

    private LocalDate dob;

    private Gender gender;

    private String phoneNo;

    private AustralianAddress australianAddress;

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
}
