package smoking.core.domain.report.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smoking.core.domain.report.domain.entity.UserReport;

import java.util.List;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    List<UserReport> findByDevice_DeviceId(String deviceId);
}
