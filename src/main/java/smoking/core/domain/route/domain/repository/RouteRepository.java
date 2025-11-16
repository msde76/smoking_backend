package smoking.core.domain.route.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smoking.core.domain.route.domain.entity.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
}
