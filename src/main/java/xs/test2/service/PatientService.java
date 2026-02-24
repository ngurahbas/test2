package xs.test2.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import xs.test2.dto.NewPatientDTO;
import xs.test2.entity.IdentifierType;
import xs.test2.entity.Patient;
import xs.test2.entity.PatientIdentifier;
import xs.test2.entity.PatientStatus;
import xs.test2.mapper.PatientMapper;
import xs.test2.repository.PatientRepository;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    @Transactional
    public Patient createPatient(NewPatientDTO dto) {
        Patient patient = patientMapper.toEntity(dto);
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setIdentifiers(new ArrayList<>());

        if (dto.getPhoneNo() != null && !dto.getPhoneNo().isBlank()) {
            PatientIdentifier identifier = new PatientIdentifier();
            identifier.setIdType(IdentifierType.PHONE);
            identifier.setIdValue(dto.getPhoneNo());
            identifier.setPatient(patient);
            patient.getIdentifiers().add(identifier);
        }

        return patientRepository.save(patient);
    }

    public Patient getPatientById(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }
}
