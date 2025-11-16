package smoking.core.domain.route.converter;

import org.springframework.stereotype.Component;
import smoking.core.domain.device.domain.entity.Device;
import smoking.core.domain.route.domain.entity.Route;
import smoking.core.domain.route.dto.RouteRequestDTO;
import smoking.core.domain.route.dto.RouteResponseDTO;
import smoking.core.domain.route.path.AvoidancePathResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RouteConverter {

    public Route toRoute(Device device, RouteRequestDTO.AvoidanceRouteDto requestDto) {

        Route route = new Route();
        route.setDevice(device);
        route.setStartLatitude(BigDecimal.valueOf(requestDto.getStartLatitude()));
        route.setStartLongitude(BigDecimal.valueOf(requestDto.getStartLongitude()));
        route.setEndLatitude(BigDecimal.valueOf(requestDto.getEndLatitude()));
        route.setEndLongitude(BigDecimal.valueOf(requestDto.getEndLongitude()));

        return route;
    }

    public RouteResponseDTO.AvoidanceRouteDto toAvoidanceRouteDto(
            Route savedRoute,
            AvoidancePathResult pathResult
    ) {

        // 1. 음성 안내 DTO 리스트 변환
        List<RouteResponseDTO.VoiceInstructionDto> voiceInstructions = pathResult.getVoiceInstructions().stream()
                .map(instr -> RouteResponseDTO.VoiceInstructionDto.builder()
                        .text(instr.getText())
                        .latitude(instr.getLatitude())
                        .longitude(instr.getLongitude())
                        .build())
                .collect(Collectors.toList());

        // 2. 회피한 흡연구역 개수 계산
        int avoidedCount = pathResult.getAvoidedAreas() != null ? pathResult.getAvoidedAreas().size() : 0;

        // 3. 최종 응답 DTO 빌드
        return RouteResponseDTO.AvoidanceRouteDto.builder()
                .routeId(savedRoute.getRouteId())
                .pathCoordinates(pathResult.getFinalPathCoordinates())
                .voiceInstructions(voiceInstructions)
                .avoidedAreasCount(avoidedCount)
                .build();
    }
}
