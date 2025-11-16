package smoking.core.domain.map.application;

import smoking.core.domain.map.DTO.ExternalRouteData;

public interface ExternalMapApiService {

    ExternalRouteData fetchBasicRoute(
            Double startLatitude, Double startLongitude,
            Double endLatitude, Double endLongitude
    );
}
