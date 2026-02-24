package xs.test2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xs.test2.entity.PatientMergeHistory;

import java.util.UUID;

@Repository
public interface PatientMergeHistoryRepository extends JpaRepository<PatientMergeHistory, UUID> {
}
