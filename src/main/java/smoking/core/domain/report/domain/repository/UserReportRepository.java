package smoking.core.domain.report.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smoking.core.domain.report.domain.entity.UserReport;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
}
