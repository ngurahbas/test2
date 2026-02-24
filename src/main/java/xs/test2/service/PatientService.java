package xs.test2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import xs.test2.dto.NewPatientDTO;
import xs.test2.shared.IdentifierType;
import xs.test2.entity.Patient;
import xs.test2.entity.PatientIdentifier;
import xs.test2.shared.PatientStatus;
import xs.test2.mapper.PatientMapper;
import xs.test2.repository.PatientRepository;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final PhoneNumberService phoneNumberService;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper,
                          PhoneNumberService phoneNumberService) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.phoneNumberService = phoneNumberService;
    }

    @Transactional
    public Patient createPatient(NewPatientDTO dto) {
        Patient patient = patientMapper.toEntity(dto);
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setIdentifiers(new ArrayList<>());

        if (dto.getPhoneNo() != null && !dto.getPhoneNo().isBlank()) {
            String normalizedPhone = phoneNumberService.normalize(dto.getPhoneNo());
            patient.setPhoneNo(normalizedPhone);

            PatientIdentifier identifier = new PatientIdentifier();
            identifier.setIdType(IdentifierType.PHONE);
            identifier.setIdValue(normalizedPhone);
            identifier.setPatient(patient);
            patient.getIdentifiers().add(identifier);
        }

        return patientRepository.save(patient);
    }

    public Patient getPatientById(UUID id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    @Transactional(readOnly = true)
    public Page<Patient> getPatients(UUID id, String name, Pageable pageable) {
        if (id != null) {
            return patientRepository.findById(id)
                    .map(p -> new PageImpl<>(java.util.List.of(p), pageable, 1))
                    .orElseGet(() -> new PageImpl<>(java.util.List.of(), pageable, 0));
        }

        if (name != null && !name.isBlank()) {
            return patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    name, name, pageable);
        }

        return patientRepository.findAll(pageable);
    }
}
