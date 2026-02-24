package xs.test2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PhoneNumberService {

    @Value("${test2.country-code}")
    private String countryCode;

    public String normalize(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return phoneNumber;
        }

        String digits = phoneNumber.replaceAll("[^0-9]", "");
        boolean hasPlus = phoneNumber.trim().startsWith("+");
        String cc = countryCode.replaceAll("[^0-9]", "");

        if (hasPlus) {
            return "+" + digits;
        }
        if (digits.startsWith(cc)) {
            return "+" + digits;
        }
        if (digits.length() == 9 && digits.startsWith("4")) {
            return "+" + cc + digits;
        }
        if (digits.length() == 10 && digits.startsWith("0")) {
            return "+" + cc + digits.substring(1);
        }

        return phoneNumber;
    }
}
