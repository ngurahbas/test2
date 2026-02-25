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

    @Query("SELECT p FROM Patient p WHERE p.id != :id " +
           "AND ((:firstName IS NOT NULL AND :lastName IS NOT NULL AND p.firstName = :firstName AND p.lastName = :lastName) " +
           "OR (:dob IS NOT NULL AND p.dob = :dob) " +
           "OR (:email IS NOT NULL AND p.email = :email) " +
           "OR (:phoneNo IS NOT NULL AND p.phoneNo = :phoneNo))")
    Iterable<Patient> getMatchingPatients(@Param("id") UUID id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("dob") LocalDate dob,
            @Param("email") String email,
            @Param("phoneNo") String phoneNo);
}
