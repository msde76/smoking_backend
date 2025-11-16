package smoking.core.domain.route.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class RouteResponseDTO {

    @Builder
    @Getter
    public static class AvoidanceRouteDto {

        private Long routeId; // Route 테이블에 저장된 ID
        private List<List<Double>> pathCoordinates; // 경로 좌표 리스트 (예: [[37.123, 127.123], ...])
        private List<VoiceInstructionDto> voiceInstructions; // TTS용 음성 안내 텍스트
        private Integer avoidedAreasCount; // 회피한 흡연구역 수
    }

    @Builder
    @Getter
    public static class VoiceInstructionDto {
        private String text; // (예: "50미터 앞 우회전")
        private Double latitude; // 안내가 필요한 지점
        private Double longitude;
    }
}
