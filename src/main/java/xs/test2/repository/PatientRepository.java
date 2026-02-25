package xs.test2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xs.test2.entity.Patient;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Page<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);

    @Query(value = "SELECT * FROM patient p WHERE p.id != :id " +
           "AND ((:firstName IS NOT NULL AND :lastName IS NOT NULL AND p.first_name = :firstName AND p.last_name = :lastName) " +
           "OR (CAST(:dob AS DATE) IS NOT NULL AND p.dob = CAST(:dob AS DATE)) " +
           "OR (:email IS NOT NULL AND p.email = :email) " +
           "OR (:phoneNo IS NOT NULL AND p.phone_no = :phoneNo))", nativeQuery = true)
    Iterable<Patient> getMatchingPatients(@Param("id") UUID id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("dob") LocalDate dob,
            @Param("email") String email,
            @Param("phoneNo") String phoneNo);
}
