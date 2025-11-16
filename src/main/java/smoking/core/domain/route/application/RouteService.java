package smoking.core.domain.route.application;

import smoking.core.domain.route.dto.RouteRequestDTO;
import smoking.core.domain.route.dto.RouteResponseDTO;

public interface RouteService {

    RouteResponseDTO.AvoidanceRouteDto findAvoidanceRoute(
            RouteRequestDTO.AvoidanceRouteDto requestDto
    );

    RouteResponseDTO.AvoidanceRouteDto findRouteByAddress(
            RouteRequestDTO.RouteByAddressDto requestDto
    );
}
