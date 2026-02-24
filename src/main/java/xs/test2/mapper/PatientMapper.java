package xs.test2.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import xs.test2.dto.NewPatientDTO;
import xs.test2.dto.PatientDTO;
import xs.test2.entity.Patient;

@Mapper(componentModel = "spring", uses = PatientIdentifierMapper.class)
public interface PatientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "identifiers", ignore = true)
    @Mapping(target = "keepMergeHistories", ignore = true)
    @Mapping(target = "discardMergeHistories", ignore = true)
    Patient toEntity(NewPatientDTO dto);

    @Mapping(target = "identifiers", source = "identifiers")
    PatientDTO toDTO(Patient patient);
}
