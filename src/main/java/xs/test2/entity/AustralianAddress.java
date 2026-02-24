package xs.test2.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AustralianAddress {

    @JsonCreator
    public AustralianAddress(@JsonProperty("address") String address,
                             @JsonProperty("suburb") String suburb,
                             @JsonProperty("state") String state,
                             @JsonProperty("postcode") String postcode) {
        this.address = address;
        this.suburb = suburb;
        this.state = state;
        this.postcode = postcode;
    }

    public AustralianAddress() {
    }

    @JsonProperty("address")
    private String address;

    @JsonProperty("suburb")
    private String suburb;

    @JsonProperty("state")
    private String state;

    @JsonProperty("postcode")
    private String postcode;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
