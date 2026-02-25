package xs.test2.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import xs.test2.dto.PatientRequestDTO;
import xs.test2.dto.NewPatientIdentifierDTO;
import xs.test2.entity.Patient;
import xs.test2.entity.PatientIdentifier;
import xs.test2.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import xs.test2.shared.Gender;
import xs.test2.shared.IdentifierType;
import xs.test2.shared.MatchScore;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PatientServiceIntegrationTest {

    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:18.2")
            .withDatabaseName("testdb");

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @BeforeAll
    static void beforeAll() {
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Test
    @Transactional
    void deletePatient_cascadeDeletesIdentifiers() {
        PatientRequestDTO patientDto = new PatientRequestDTO();
        patientDto.setFirstName("Test");
        patientDto.setLastName("User");
        patientDto.setGender(Gender.MALE);

        Patient patient = patientService.createPatient(patientDto);
        UUID patientId = patient.getId();

        NewPatientIdentifierDTO emailIdentifier = new NewPatientIdentifierDTO();
        emailIdentifier.setIdType(IdentifierType.EMAIL);
        emailIdentifier.setIdValue("test@example.com");
        patientService.addIdentifier(patientId, emailIdentifier);

        NewPatientIdentifierDTO mrnIdentifier = new NewPatientIdentifierDTO();
        mrnIdentifier.setIdType(IdentifierType.MRN);
        mrnIdentifier.setIdValue("12345");
        patientService.addIdentifier(patientId, mrnIdentifier);

        List<PatientIdentifier> identifiers = patientService.getIdentifiers(patientId);
        assertThat(identifiers).hasSize(2);

        patientService.deletePatient(patientId);

        assertThat(patientRepository.findById(patientId)).isEmpty();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> patientService.getIdentifiers(patientId));
        assertThat(exception.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    @Transactional
    void deleteIdentifier_whenPhoneIdentifierMatchesPatientPhone_shouldThrowBadRequest() {
        PatientRequestDTO patientDto = new PatientRequestDTO();
        patientDto.setFirstName("Test");
        patientDto.setLastName("User");
        patientDto.setGender(Gender.MALE);
        patientDto.setPhoneNo("0412345678");

        Patient patient = patientService.createPatient(patientDto);
        UUID patientId = patient.getId();

        List<PatientIdentifier> identifiers = patientService.getIdentifiers(patientId);
        PatientIdentifier phoneIdentifier = identifiers.stream()
                .filter(i -> i.getIdType() == IdentifierType.PHONE)
                .findFirst()
                .orElseThrow();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> patientService.deleteIdentifier(patientId, phoneIdentifier.getId()));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Transactional
    void autoMatchPatient_whenPatientHasMatchingRecords_shouldReturnCorrectMatchScore() {
        PatientRequestDTO patientX = new PatientRequestDTO();
        patientX.setFirstName("John");
        patientX.setLastName("Smith");
        patientX.setDob(LocalDate.of(1990, 1, 1));
        patientX.setGender(Gender.MALE);
        patientX.setEmail("john@test.com");
        patientX.setPhoneNo("0411111111");
        Patient x = patientService.createPatient(patientX);

        PatientRequestDTO patientY = new PatientRequestDTO();
        patientY.setFirstName("Jane");
        patientY.setLastName("Doe");
        patientY.setDob(LocalDate.of(1985, 5, 15));
        patientY.setGender(Gender.FEMALE);
        patientY.setEmail("jane@test.com");
        patientY.setPhoneNo("0422222222");
        patientService.createPatient(patientY);

        PatientRequestDTO patientZ = new PatientRequestDTO();
        patientZ.setFirstName("Bob");
        patientZ.setLastName("Brown");
        patientZ.setDob(LocalDate.of(1992, 3, 20));
        patientZ.setGender(Gender.MALE);
        patientZ.setEmail("bob@test.com");
        patientZ.setPhoneNo("0433333333");
        Patient z = patientService.createPatient(patientZ);

        PatientRequestDTO patientW = new PatientRequestDTO();
        patientW.setFirstName("John");
        patientW.setLastName("Smith");
        patientW.setDob(LocalDate.of(1980, 1, 1));
        patientW.setGender(Gender.MALE);
        patientW.setEmail("john@test.com");
        patientW.setPhoneNo("0444444444");
        Patient w = patientService.createPatient(patientW);

        PatientRequestDTO patientA = new PatientRequestDTO();
        patientA.setFirstName("John");
        patientA.setLastName("Smith");
        patientA.setDob(LocalDate.of(1990, 1, 1));
        patientA.setGender(Gender.MALE);
        patientA.setEmail("john@test.com");
        patientA.setPhoneNo("0433333333");
        Patient a = patientService.createPatient(patientA);

        Map<UUID, MatchScore> results = patientService.autoMatchPatient(a);

        assertThat(results).hasSize(3);
        assertThat(results.get(x.getId())).isEqualTo(MatchScore.AUTO_MATCH);
        assertThat(results.get(w.getId())).isEqualTo(MatchScore.REVIEW);
        assertThat(results.get(z.getId())).isEqualTo(MatchScore.NO_MATCH);
    }
}
