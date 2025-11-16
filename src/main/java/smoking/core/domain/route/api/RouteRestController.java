package smoking.core.domain.route.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smoking.core.domain.route.application.RouteService;
import smoking.core.domain.route.dto.RouteRequestDTO;
import smoking.core.domain.route.dto.RouteResponseDTO;
import smoking.core.global.common.response.BaseResponse;
import smoking.core.global.error.code.status.SuccessStatus;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteRestController {

    public final RouteService routeService;

    @PostMapping("/avoidance")
    @Operation(summary = "흡연 구역 회피 경로 조회 API", description = "사용자가 가는 경로 내의 흡연 구역을 회피하는 경로 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMOKING_200", description = "OK, 성공적으로 조회되었습니다.")
    })
    public BaseResponse<RouteResponseDTO.AvoidanceRouteDto> avoidSmokingAreas(
            @RequestBody RouteRequestDTO.AvoidanceRouteDto requestDto
    ) {
        RouteResponseDTO.AvoidanceRouteDto result = routeService.findAvoidanceRoute(requestDto);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }

    @PostMapping("/address")
    @Operation(summary = "주소 기반 경로 탐색 및 회피 API", description = "프론트에서 받은 주소 텍스트를 Geocoding 후 경로 탐색")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ROUTE_200", description = "OK, 성공적으로 경로를 탐색했습니다.")
    })
    public BaseResponse<RouteResponseDTO.AvoidanceRouteDto> findRouteByAddress(
            @RequestBody RouteRequestDTO.RouteByAddressDto requestDto
    ) {
        RouteResponseDTO.AvoidanceRouteDto result = routeService.findRouteByAddress(requestDto);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }
}
