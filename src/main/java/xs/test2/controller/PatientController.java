package xs.test2.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xs.test2.dto.NewPatientIdentifierDTO;
import xs.test2.dto.PatientRequestDTO;
import xs.test2.dto.PatientDTO;
import xs.test2.dto.PatientIdentifierDTO;
import xs.test2.dto.PatientListEntryDTO;
import xs.test2.mapper.PatientIdentifierMapper;
import xs.test2.mapper.PatientMapper;
import xs.test2.service.PatientService;

import java.util.List;
import java.util.UUID;

@RestController
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final PatientIdentifierMapper patientIdentifierMapper;

    public PatientController(PatientService patientService, PatientMapper patientMapper,
                            PatientIdentifierMapper patientIdentifierMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.patientIdentifierMapper = patientIdentifierMapper;
    }

    @GetMapping("/api/patient")
    public Page<PatientListEntryDTO> getPatients(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        var patients = patientService.getPatients(id, name, pageable);
        return patients.map(patientMapper::toListEntryDTO);
    }

    @PostMapping("/api/patient")
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDTO createPatient(@Valid @RequestBody PatientRequestDTO dto) {
        var patient = patientService.createPatient(dto);
        return patientMapper.toDTO(patient);
    }

    @GetMapping("/api/patient/{id}")
    public PatientDTO getPatient(@PathVariable UUID id) {
        var patient = patientService.getPatientById(id);
        return patientMapper.toDTO(patient);
    }

    @DeleteMapping("/api/patient/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
    }

    @GetMapping("/api/patient/{id}/identifier")
    public List<PatientIdentifierDTO> getIdentifiers(@PathVariable UUID id) {
        var identifiers = patientService.getIdentifiers(id);
        return identifiers.stream()
                .map(patientIdentifierMapper::toDTO)
                .toList();
    }

    @PostMapping("/api/patient/{id}/identifier")
    @ResponseStatus(HttpStatus.CREATED)
    public PatientIdentifierDTO addIdentifier(@PathVariable UUID id,
                                               @Valid @RequestBody NewPatientIdentifierDTO dto) {
        var identifier = patientService.addIdentifier(id, dto);
        return patientIdentifierMapper.toDTO(identifier);
    }

    @DeleteMapping("/api/patient/{id}/identifier/{identifierId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIdentifier(@PathVariable UUID id, @PathVariable UUID identifierId) {
        patientService.deleteIdentifier(id, identifierId);
    }
}
