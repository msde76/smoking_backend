package smoking.core.domain.smoking.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smoking.core.domain.smoking.domain.entity.AreaStatus;
import smoking.core.domain.smoking.domain.entity.SmokingArea;

import java.math.BigDecimal;
import java.util.List;

public interface SmokingAreaRepository extends JpaRepository<SmokingArea, Long> {

    List<SmokingArea> findAllByStatus(AreaStatus status);

    List<SmokingArea> findAllByLatitudeBetweenAndLongitudeBetweenAndStatus(
            BigDecimal minLat, BigDecimal maxLat,
            BigDecimal minLng, BigDecimal maxLng,
            AreaStatus status
    );
}
