package xs.test2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import xs.test2.dto.NewPatientIdentifierDTO;
import xs.test2.dto.PatientRequestDTO;
import xs.test2.shared.IdentifierType;
import xs.test2.entity.Patient;
import xs.test2.entity.PatientIdentifier;
import xs.test2.shared.PatientStatus;
import xs.test2.mapper.PatientMapper;
import xs.test2.repository.PatientRepository;

import java.util.ArrayList;
import java.util.List;
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
    public void deletePatient(UUID id) {
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
    }

    @Transactional
    public Patient createPatient(PatientRequestDTO dto) {
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

    @Transactional
    public PatientIdentifier addIdentifier(UUID patientId, NewPatientIdentifierDTO dto) {
        Patient patient = getPatientById(patientId);

        String idValue = dto.getIdValue();
        if (dto.getIdType() == IdentifierType.PHONE && idValue != null && !idValue.isBlank()) {
            idValue = phoneNumberService.normalize(idValue);
        }

        PatientIdentifier identifier = new PatientIdentifier();
        identifier.setIdType(dto.getIdType());
        identifier.setIdValue(idValue);
        identifier.setPatient(patient);
        patient.getIdentifiers().add(identifier);

        return patientRepository.save(patient).getIdentifiers().getLast();
    }

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

    public List<PatientIdentifier> getIdentifiers(UUID patientId) {
        Patient patient = getPatientById(patientId);
        return new ArrayList<>(patient.getIdentifiers());
    }

    @Transactional
    public Patient updatePatient(UUID id, PatientRequestDTO dto) {
        Patient patient = getPatientById(id);

        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setDob(dto.getDob());
        patient.setGender(dto.getGender());
        patient.setAustralianAddress(dto.getAustralianAddress());

        String oldPhone = patient.getPhoneNo();
        String newPhone = dto.getPhoneNo();
        String normalizedNewPhone = null;

        if (newPhone != null && !newPhone.isBlank()) {
            normalizedNewPhone = phoneNumberService.normalize(newPhone);
        }

        boolean phoneChanged = (oldPhone == null && normalizedNewPhone != null)
                || (oldPhone != null && !oldPhone.equals(normalizedNewPhone));

        if (phoneChanged) {
            patient.setPhoneNo(normalizedNewPhone);

            patient.getIdentifiers().stream()
                    .filter(i -> i.getIdType() == IdentifierType.PHONE)
                    .findFirst()
                    .ifPresent(patient.getIdentifiers()::remove);

            if (normalizedNewPhone != null) {
                PatientIdentifier identifier = new PatientIdentifier();
                identifier.setIdType(IdentifierType.PHONE);
                identifier.setIdValue(normalizedNewPhone);
                identifier.setPatient(patient);
                patient.getIdentifiers().add(identifier);
            }
        }

        return patientRepository.save(patient);
    }

    @Transactional
    public void deleteIdentifier(UUID patientId, UUID identifierId) {
        Patient patient = getPatientById(patientId);
        PatientIdentifier identifier = patient.getIdentifiers().stream()
                .filter(i -> i.getId().equals(identifierId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Identifier not found"));
        patient.getIdentifiers().remove(identifier);
        patientRepository.save(patient);
    }
}
