package xs.test2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PhoneNumberServiceTest {

    @InjectMocks
    private PhoneNumberService phoneNumberService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(phoneNumberService, "countryCode", "+61");
    }

    @ParameterizedTest
    @CsvSource({
        "+61 412 345 678, +61412345678",
        "0412 345 678, +61412345678",
        "61412345678, +61412345678",
        "+61412345678, +61412345678",
        "+61-412-345-678, +61412345678",
        "0412-345-678, +61412345678",
        "614-123-456-78, +61412345678",
        "(0412) 345 678, +61412345678",
        "0412.345.678, +61412345678",
        "+1-212-555-1234, +12125551234",
        "+44 20 7946 0958, +442079460958",
        "+81-3-1234-5678, +81312345678"
    })
    void normalize_validPhoneNumbers_returnsNormalized(String input, String expected) {
        assertEquals(expected, phoneNumberService.normalize(input));
    }

    @Test
    void normalize_withNull_returnsNull() {
        assertNull(phoneNumberService.normalize(null));
    }

    @Test
    void normalize_withBlank_returnsBlank() {
        assertEquals("   ", phoneNumberService.normalize("   "));
    }

    @ParameterizedTest
    @CsvSource({
        "412345678, +61412345678",
        "041234567, 041234567"
    })
    void normalize_9DigitNumbers_returnsNormalized(String input, String expected) {
        assertEquals(expected, phoneNumberService.normalize(input));
    }

    @Test
    void normalize_noPatternMatch_returnsOriginal() {
        assertEquals("1234567890", phoneNumberService.normalize("1234567890"));
    }

    @Test
    void normalize_withNonDigits_returnsOriginal() {
        assertEquals("ABC-DEF", phoneNumberService.normalize("ABC-DEF"));
    }

    @Test
    void normalize_startsWithCountryCodeNoPlus_returnsNormalized() {
        assertEquals("+61234567890", phoneNumberService.normalize("61234567890"));
    }
}
