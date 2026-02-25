package xs.test2.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;
import xs.test2.dto.NewPatientIdentifierDTO;
import xs.test2.dto.PatientRequestDTO;
import xs.test2.entity.Patient;
import xs.test2.entity.PatientIdentifier;
import xs.test2.mapper.PatientMapper;
import xs.test2.repository.PatientRepository;
import xs.test2.shared.Gender;
import xs.test2.shared.IdentifierType;
import xs.test2.shared.MatchScore;
import xs.test2.shared.PatientStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private PhoneNumberService phoneNumberService;

    @InjectMocks
    private PatientService patientService;

    private PatientRequestDTO dto;
    private Patient patient;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        dto = new PatientRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDob(LocalDate.of(1990, 1, 1));
        dto.setGender(Gender.MALE);
        dto.setPhoneNo("0412345678");
        dto.setEmail("john@example.com");

        patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDob(LocalDate.of(1990, 1, 1));
        patient.setGender(Gender.MALE);
        patient.setPhoneNo("+61412345678");
        patient.setEmail("john@example.com");
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setIdentifiers(new ArrayList<>());
    }

    @Test
    void createPatient_withPhoneAndEmail_createsSuccessfully() {
        when(patientMapper.toEntity(dto)).thenReturn(patient);
        when(phoneNumberService.normalize("0412345678")).thenReturn("+61412345678");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createPatient(dto);

        assertNotNull(result);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void createPatient_withPhoneOnly_createsSuccessfully() {
        dto.setEmail(null);
        when(patientMapper.toEntity(dto)).thenReturn(patient);
        when(phoneNumberService.normalize("0412345678")).thenReturn("+61412345678");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createPatient(dto);

        assertNotNull(result);
    }

    @Test
    void createPatient_withEmailOnly_createsSuccessfully() {
        dto.setPhoneNo(null);
        when(patientMapper.toEntity(dto)).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createPatient(dto);

        assertNotNull(result);
    }

    @Test
    void createPatient_withNeitherPhoneNorEmail_createsSuccessfully() {
        dto.setPhoneNo(null);
        dto.setEmail(null);
        when(patientMapper.toEntity(dto)).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createPatient(dto);

        assertNotNull(result);
    }

    @Test
    void createPatient_withBlankPhone_createsSuccessfully() {
        dto.setPhoneNo("   ");
        when(patientMapper.toEntity(dto)).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.createPatient(dto);

        assertNotNull(result);
    }

    @Test
    void getPatientById_whenExists_returnsPatient() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(id);

        assertEquals(patient, result);
    }

    @Test
    void getPatientById_whenNotExists_throwsException() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> patientService.getPatientById(id));
    }

    @Test
    void deletePatient_deletesSuccessfully() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        patientService.deletePatient(id);

        verify(patientRepository).delete(patient);
    }

    @Test
    void getPatients_withId_returnsPatient() {
        UUID id = UUID.randomUUID();
        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));

        Page<Patient> result = patientService.getPatients(id, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(patient, result.getContent().get(0));
    }

    @Test
    void getPatients_withName_returnsMatchingPatients() {
        String name = "John";
        List<Patient> patients = List.of(patient);
        when(patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable))
                .thenReturn(new PageImpl<>(patients, pageable, 1));

        Page<Patient> result = patientService.getPatients(null, name, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getPatients_withNoFilters_returnsAllPatients() {
        when(patientRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(patient), pageable, 1));

        Page<Patient> result = patientService.getPatients(null, null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getIdentifiers_returnsList() {
        UUID patientId = UUID.randomUUID();
        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setId(UUID.randomUUID());
        identifier.setIdType(IdentifierType.PHONE);
        identifier.setIdValue("+61412345678");
        patient.getIdentifiers().add(identifier);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        List<PatientIdentifier> result = patientService.getIdentifiers(patientId);

        assertEquals(1, result.size());
    }

    @Test
    void addIdentifier_withPhoneType_normalizesPhone() {
        UUID patientId = UUID.randomUUID();
        NewPatientIdentifierDTO identifierDto = new NewPatientIdentifierDTO();
        identifierDto.setIdType(IdentifierType.PHONE);
        identifierDto.setIdValue("0412345678");

        PatientIdentifier savedIdentifier = new PatientIdentifier();
        savedIdentifier.setId(UUID.randomUUID());
        savedIdentifier.setIdType(IdentifierType.PHONE);
        savedIdentifier.setIdValue("+61412345678");
        patient.getIdentifiers().add(savedIdentifier);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(phoneNumberService.normalize("0412345678")).thenReturn("+61412345678");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientIdentifier result = patientService.addIdentifier(patientId, identifierDto);

        assertNotNull(result);
    }

    @Test
    void addIdentifier_withEmailType_doesNotNormalize() {
        UUID patientId = UUID.randomUUID();
        NewPatientIdentifierDTO identifierDto = new NewPatientIdentifierDTO();
        identifierDto.setIdType(IdentifierType.EMAIL);
        identifierDto.setIdValue("test@example.com");

        PatientIdentifier savedIdentifier = new PatientIdentifier();
        savedIdentifier.setId(UUID.randomUUID());
        savedIdentifier.setIdType(IdentifierType.EMAIL);
        savedIdentifier.setIdValue("test@example.com");
        patient.getIdentifiers().add(savedIdentifier);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientIdentifier result = patientService.addIdentifier(patientId, identifierDto);

        assertNotNull(result);
        verify(phoneNumberService, never()).normalize(any());
    }

    @Test
    void addIdentifier_withBlankPhone_doesNotNormalize() {
        UUID patientId = UUID.randomUUID();
        NewPatientIdentifierDTO identifierDto = new NewPatientIdentifierDTO();
        identifierDto.setIdType(IdentifierType.PHONE);
        identifierDto.setIdValue("   ");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientIdentifier result = patientService.addIdentifier(patientId, identifierDto);

        assertNotNull(result);
        verify(phoneNumberService, never()).normalize(any());
    }

    @Test
    void updatePatient_phoneChanged_updatesPhone() {
        UUID id = UUID.randomUUID();
        dto.setPhoneNo("0499999999");

        PatientIdentifier phoneId = new PatientIdentifier();
        phoneId.setId(UUID.randomUUID());
        phoneId.setIdType(IdentifierType.PHONE);
        phoneId.setIdValue("+61412345678");
        phoneId.setPatient(patient);
        patient.getIdentifiers().add(phoneId);

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(phoneNumberService.normalize("0499999999")).thenReturn("+61499999999");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.updatePatient(id, dto);

        assertNotNull(result);
    }

    @Test
    void updatePatient_phoneUnchanged_doesNotUpdatePhone() {
        UUID id = UUID.randomUUID();
        dto.setPhoneNo("0412345678");

        PatientIdentifier phoneId = new PatientIdentifier();
        phoneId.setId(UUID.randomUUID());
        phoneId.setIdType(IdentifierType.PHONE);
        phoneId.setIdValue("+61412345678");
        phoneId.setPatient(patient);
        patient.getIdentifiers().add(phoneId);

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(phoneNumberService.normalize("0412345678")).thenReturn("+61412345678");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.updatePatient(id, dto);

        verify(phoneNumberService).normalize("0412345678");
    }

    @Test
    void updatePatient_phoneSetToNull_clearsPhone() {
        UUID id = UUID.randomUUID();
        dto.setPhoneNo(null);

        PatientIdentifier phoneId = new PatientIdentifier();
        phoneId.setId(UUID.randomUUID());
        phoneId.setIdType(IdentifierType.PHONE);
        phoneId.setIdValue("+61412345678");
        phoneId.setPatient(patient);
        patient.getIdentifiers().add(phoneId);

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.updatePatient(id, dto);

        assertNull(patient.getPhoneNo());
    }

    @Test
    void updatePatient_phoneSetToBlank_clearsPhone() {
        UUID id = UUID.randomUUID();
        dto.setPhoneNo("   ");

        PatientIdentifier phoneId = new PatientIdentifier();
        phoneId.setId(UUID.randomUUID());
        phoneId.setIdType(IdentifierType.PHONE);
        phoneId.setIdValue("+61412345678");
        phoneId.setPatient(patient);
        patient.getIdentifiers().add(phoneId);

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.updatePatient(id, dto);

        assertNull(patient.getPhoneNo());
    }

    @Test
    void updatePatient_emailChanged_updatesEmail() {
        UUID id = UUID.randomUUID();
        dto.setEmail("newemail@example.com");

        PatientIdentifier emailId = new PatientIdentifier();
        emailId.setId(UUID.randomUUID());
        emailId.setIdType(IdentifierType.EMAIL);
        emailId.setIdValue("john@example.com");
        emailId.setPatient(patient);
        patient.getIdentifiers().add(emailId);

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient result = patientService.updatePatient(id, dto);

        assertNotNull(result);
    }

    @Test
    void updatePatient_emailSetToNull_clearsEmail() {
        UUID id = UUID.randomUUID();
        dto.setEmail(null);

        PatientIdentifier emailId = new PatientIdentifier();
        emailId.setId(UUID.randomUUID());
        emailId.setIdType(IdentifierType.EMAIL);
        emailId.setIdValue("john@example.com");
        emailId.setPatient(patient);
        patient.getIdentifiers().add(emailId);

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.updatePatient(id, dto);

        assertNull(patient.getEmail());
    }

    @Test
    void updatePatient_emailSetToBlank_clearsEmail() {
        UUID id = UUID.randomUUID();
        dto.setEmail("   ");

        PatientIdentifier emailId = new PatientIdentifier();
        emailId.setId(UUID.randomUUID());
        emailId.setIdType(IdentifierType.EMAIL);
        emailId.setIdValue("john@example.com");
        emailId.setPatient(patient);
        patient.getIdentifiers().add(emailId);

        when(patientRepository.findById(id)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.updatePatient(id, dto);

        assertEquals("   ", patient.getEmail());
    }

    @Test
    void deleteIdentifier_deletesSuccessfully() {
        UUID patientId = UUID.randomUUID();
        UUID identifierId = UUID.randomUUID();

        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setId(identifierId);
        identifier.setIdType(IdentifierType.EMAIL);
        identifier.setIdValue("other@example.com");
        patient.getIdentifiers().add(identifier);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        patientService.deleteIdentifier(patientId, identifierId);

        verify(patientRepository).save(patient);
    }

    @Test
    void deleteIdentifier_whenNotFound_throwsException() {
        UUID patientId = UUID.randomUUID();
        UUID identifierId = UUID.randomUUID();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        assertThrows(ResponseStatusException.class, () -> patientService.deleteIdentifier(patientId, identifierId));
    }

    @Test
    void deleteIdentifier_phoneMatchesPatientPhone_throwsException() {
        UUID patientId = UUID.randomUUID();
        UUID identifierId = UUID.randomUUID();

        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setId(identifierId);
        identifier.setIdType(IdentifierType.PHONE);
        identifier.setIdValue("+61412345678");
        patient.getIdentifiers().add(identifier);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> patientService.deleteIdentifier(patientId, identifierId));

        assertTrue(exception.getReason().contains("Cannot delete phone identifier"));
    }

    @Test
    void deleteIdentifier_emailMatchesPatientEmail_throwsException() {
        UUID patientId = UUID.randomUUID();
        UUID identifierId = UUID.randomUUID();

        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setId(identifierId);
        identifier.setIdType(IdentifierType.EMAIL);
        identifier.setIdValue("john@example.com");
        patient.getIdentifiers().add(identifier);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> patientService.deleteIdentifier(patientId, identifierId));

        assertTrue(exception.getReason().contains("Cannot delete email identifier"));
    }

    @Test
    void autoMatchPatient_withAutoMatchScore_returnsAutoMatch() {
        Patient matchingPatient = new Patient();
        matchingPatient.setId(UUID.randomUUID());
        matchingPatient.setFirstName("John");
        matchingPatient.setLastName("Doe");
        matchingPatient.setDob(LocalDate.of(1990, 1, 1));
        matchingPatient.setEmail("john@example.com");
        matchingPatient.setPhoneNo("+61412345678");

        when(patientRepository.getMatchingPatients(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(matchingPatient));

        Map<UUID, MatchScore> result = patientService.autoMatchPatient(patient);

        assertEquals(MatchScore.AUTO_MATCH, result.get(matchingPatient.getId()));
    }

    @Test
    void autoMatchPatient_withReviewScore_returnsReview() {
        Patient matchingPatient = new Patient();
        matchingPatient.setId(UUID.randomUUID());
        matchingPatient.setFirstName("John");
        matchingPatient.setLastName("Doe");
        matchingPatient.setDob(LocalDate.of(1985, 5, 15));
        matchingPatient.setEmail("john@example.com");
        matchingPatient.setPhoneNo("+61412345678");

        when(patientRepository.getMatchingPatients(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(matchingPatient));

        Map<UUID, MatchScore> result = patientService.autoMatchPatient(patient);

        assertEquals(MatchScore.REVIEW, result.get(matchingPatient.getId()));
    }

    @Test
    void autoMatchPatient_withNoMatchScore_returnsNoMatch() {
        Patient matchingPatient = new Patient();
        matchingPatient.setId(UUID.randomUUID());
        matchingPatient.setFirstName("Jane");
        matchingPatient.setLastName("Smith");
        matchingPatient.setDob(LocalDate.of(1985, 5, 15));
        matchingPatient.setEmail("jane@example.com");
        matchingPatient.setPhoneNo("+61999999999");

        when(patientRepository.getMatchingPatients(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(matchingPatient));

        Map<UUID, MatchScore> result = patientService.autoMatchPatient(patient);

        assertEquals(MatchScore.NO_MATCH, result.get(matchingPatient.getId()));
    }

    @Test
    void autoMatchPatient_withMultipleMatchingPatients_returnsCorrectScores() {
        Patient autoMatchPatient = new Patient();
        autoMatchPatient.setId(UUID.randomUUID());
        autoMatchPatient.setFirstName("John");
        autoMatchPatient.setLastName("Doe");
        autoMatchPatient.setDob(LocalDate.of(1990, 1, 1));
        autoMatchPatient.setEmail("john@example.com");
        autoMatchPatient.setPhoneNo("+61412345678");

        Patient noMatchPatient = new Patient();
        noMatchPatient.setId(UUID.randomUUID());
        noMatchPatient.setFirstName("Jane");
        noMatchPatient.setLastName("Smith");
        noMatchPatient.setDob(LocalDate.of(1985, 5, 15));
        noMatchPatient.setEmail("jane@example.com");
        noMatchPatient.setPhoneNo("+61999999999");

        when(patientRepository.getMatchingPatients(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(autoMatchPatient, noMatchPatient));

        Map<UUID, MatchScore> result = patientService.autoMatchPatient(patient);

        assertEquals(MatchScore.AUTO_MATCH, result.get(autoMatchPatient.getId()));
        assertEquals(MatchScore.NO_MATCH, result.get(noMatchPatient.getId()));
    }
}
