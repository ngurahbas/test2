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
import xs.test2.dto.NewPatientDTO;
import xs.test2.dto.NewPatientIdentifierDTO;
import xs.test2.entity.Patient;
import xs.test2.entity.PatientIdentifier;
import xs.test2.repository.PatientRepository;
import xs.test2.shared.Gender;
import xs.test2.shared.IdentifierType;

import java.util.List;
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
        NewPatientDTO patientDto = new NewPatientDTO();
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
}
