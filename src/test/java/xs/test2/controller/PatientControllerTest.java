package xs.test2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import xs.test2.dto.NewPatientIdentifierDTO;
import xs.test2.dto.PatientDTO;
import xs.test2.dto.PatientIdentifierDTO;
import xs.test2.dto.PatientListEntryDTO;
import xs.test2.dto.PatientRequestDTO;
import xs.test2.entity.Patient;
import xs.test2.entity.PatientIdentifier;
import xs.test2.mapper.PatientIdentifierMapper;
import xs.test2.mapper.PatientMapper;
import xs.test2.service.PatientService;
import xs.test2.shared.Gender;
import xs.test2.shared.IdentifierType;
import xs.test2.shared.PatientStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PatientService patientService;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private PatientIdentifierMapper patientIdentifierMapper;

    @InjectMocks
    private PatientController patientController;

    private Patient testPatient;
    private PatientDTO testPatientDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
        objectMapper = new ObjectMapper();

        testPatient = createTestPatient();
        testPatientDTO = createTestPatientDTO();
    }

    @Test
    void getPatients_withId_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(List.of(testPatient), pageable, 1);
        PatientListEntryDTO dto = new PatientListEntryDTO();

        when(patientService.getPatients(id, null, pageable)).thenReturn(patientPage);
        when(patientMapper.toListEntryDTO(testPatient)).thenReturn(dto);

        mockMvc.perform(get("/api/patient")
                        .param("id", id.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getPatients_withName_returnsOk() throws Exception {
        String name = "John";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(List.of(testPatient), pageable, 1);
        PatientListEntryDTO dto = new PatientListEntryDTO();

        when(patientService.getPatients(null, name, pageable)).thenReturn(patientPage);
        when(patientMapper.toListEntryDTO(testPatient)).thenReturn(dto);

        mockMvc.perform(get("/api/patient")
                        .param("name", name))
                .andExpect(status().isOk());
    }

    @Test
    void getPatients_withPagination_returnsOk() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patient> patientPage = new PageImpl<>(List.of(), pageable, 0);

        when(patientService.getPatients(any(), any(), any())).thenReturn(patientPage);

        mockMvc.perform(get("/api/patient")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void createPatient_withValidData_returnsCreated() throws Exception {
        PatientRequestDTO requestDTO = createPatientRequestDTO();

        when(patientService.createPatient(any(PatientRequestDTO.class))).thenReturn(testPatient);
        when(patientMapper.toDTO(testPatient)).thenReturn(testPatientDTO);

        mockMvc.perform(post("/api/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updatePatient_withValidData_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();

        when(patientService.updatePatient(eq(id), any(PatientRequestDTO.class))).thenReturn(testPatient);
        when(patientMapper.toDTO(testPatient)).thenReturn(testPatientDTO);

        mockMvc.perform(put("/api/patient/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void getPatient_withValidId_returnsOk() throws Exception {
        UUID id = UUID.randomUUID();

        when(patientService.getPatientById(id)).thenReturn(testPatient);
        when(patientMapper.toDTO(testPatient)).thenReturn(testPatientDTO);

        mockMvc.perform(get("/api/patient/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void deletePatient_withValidId_returnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/patient/{id}", id))
                .andExpect(status().isNoContent());

        verify(patientService).deletePatient(id);
    }

    @Test
    void getIdentifiers_withValidId_returnsOk() throws Exception {
        UUID patientId = UUID.randomUUID();
        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setId(UUID.randomUUID());
        identifier.setIdType(IdentifierType.PHONE);
        identifier.setIdValue("+61412345678");
        PatientIdentifierDTO dto = new PatientIdentifierDTO();

        when(patientService.getIdentifiers(patientId)).thenReturn(List.of(identifier));
        when(patientIdentifierMapper.toDTO(identifier)).thenReturn(dto);

        mockMvc.perform(get("/api/patient/{id}/identifier", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void addIdentifier_withValidData_returnsCreated() throws Exception {
        UUID patientId = UUID.randomUUID();
        NewPatientIdentifierDTO requestDTO = new NewPatientIdentifierDTO();
        requestDTO.setIdType(IdentifierType.PHONE);
        requestDTO.setIdValue("+61412345678");

        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setId(UUID.randomUUID());
        PatientIdentifierDTO responseDTO = new PatientIdentifierDTO();

        when(patientService.addIdentifier(eq(patientId), any(NewPatientIdentifierDTO.class))).thenReturn(identifier);
        when(patientIdentifierMapper.toDTO(identifier)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/patient/{id}/identifier", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void deleteIdentifier_withValidId_returnsNoContent() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID identifierId = UUID.randomUUID();

        mockMvc.perform(delete("/api/patient/{id}/identifier/{identifierId}", patientId, identifierId))
                .andExpect(status().isNoContent());

        verify(patientService).deleteIdentifier(patientId, identifierId);
    }

    private Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDob(LocalDate.of(1990, 1, 1));
        patient.setGender(Gender.MALE);
        patient.setPhoneNo("+61412345678");
        patient.setEmail("john@example.com");
        patient.setStatus(PatientStatus.ACTIVE);
        return patient;
    }

    private PatientDTO createTestPatientDTO() {
        PatientDTO dto = new PatientDTO();
        dto.setId(testPatient.getId());
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDob(LocalDate.of(1990, 1, 1));
        dto.setGender(Gender.MALE);
        dto.setPhoneNo("+61412345678");
        dto.setEmail("john@example.com");
        return dto;
    }

    private PatientRequestDTO createPatientRequestDTO() {
        PatientRequestDTO dto = new PatientRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDob(LocalDate.of(1990, 1, 1));
        dto.setGender(Gender.MALE);
        dto.setPhoneNo("0412345678");
        dto.setEmail("john@example.com");
        return dto;
    }
}
