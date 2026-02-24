package xs.test2.mapper;

import org.mapstruct.Mapper;
import xs.test2.dto.PatientIdentifierDTO;
import xs.test2.entity.PatientIdentifier;

@Mapper(componentModel = "spring")
public interface PatientIdentifierMapper {

    PatientIdentifierDTO toDTO(PatientIdentifier identifier);
}
