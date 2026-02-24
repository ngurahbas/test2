package xs.test2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xs.test2.shared.Gender;
import xs.test2.shared.IdentifierType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController

public class EnumController {

    @GetMapping("/api/enum/gender")
    public List<Map<String, String>> getGenders() {
        return Arrays.stream(Gender.values())
                .map(gender -> Map.of(
                        "value", gender.name(),
                        "label", switch (gender) {
                            case FEMALE -> "Female";
                            case MALE -> "Male";
                            case OTHER -> "Other";
                        }
                ))
                .toList();
    }

    @GetMapping("/api/enum/identifier-type")
    public List<Map<String, String>> getIdentifierTypes() {
        return Arrays.stream(IdentifierType.values())
                .map(type -> Map.of(
                        "value", type.name(),
                        "label", switch (type) {
                            case MRN -> "Mrn";
                            case NATIONAL_ID -> "National ID";
                            case PHONE -> "Phone";
                            case EMAIL -> "Email";
                        }
                ))
                .toList();
    }
}
