package xs.test2.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xs.test2.dto.NewPatientDTO;
import xs.test2.dto.PatientDTO;
import xs.test2.mapper.PatientMapper;
import xs.test2.service.PatientService;

import java.util.UUID;

@RestController
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    public PatientController(PatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }

    @PostMapping("/api/patient")
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDTO createPatient(@Valid @RequestBody NewPatientDTO dto) {
        var patient = patientService.createPatient(dto);
        return patientMapper.toDTO(patient);
    }

    @GetMapping("/api/patient/{id}")
    public PatientDTO getPatient(@PathVariable UUID id) {
        var patient = patientService.getPatientById(id);
        return patientMapper.toDTO(patient);
    }
}
