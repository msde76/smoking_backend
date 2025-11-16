package smoking.core.domain.route.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smoking.core.domain.route.domain.entity.RouteSmokingArea;

public interface RouteSmokingRepository extends JpaRepository<RouteSmokingArea, Long> {
}
