package smoking.core.domain.route.path;

import lombok.Getter;
import smoking.core.domain.smoking.domain.entity.SmokingArea;
import java.util.Collections;
import java.util.List;

/**
 * PathFinder가 A* 알고リズム을 실행한 후,
 * 최종 경로, 음성 안내, 회피 구역 목록을 담아 반환하는 DTO 클래스입니다.
 */
@Getter
public class AvoidancePathResult {

    // 1. 최종 회피 경로 좌표 리스트 (예: [[lng, lat], [lng, lat], ...])
    private final List<List<Double>> finalPathCoordinates;

    // 2. 음성 안내 지시사항 목록
    private final List<Instruction> voiceInstructions;

    // 3. 이 경로 계산으로 인해 실제로 회피된 흡연 구역 엔티티 목록
    private final List<SmokingArea> avoidedAreas;

    /**
     * 음성 안내(TTS)용 텍스트와 해당 지점의 좌표를 담는 내부 DTO
     */
    @Getter
    public static class Instruction {
        private final String text;
        private final Double latitude;
        private final Double longitude;

        public Instruction(String text, Double latitude, Double longitude) {
            this.text = text;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    /**
     * 생성자
     */
    public AvoidancePathResult(
            List<List<Double>> finalPathCoordinates,
            List<Instruction> voiceInstructions,
            List<SmokingArea> avoidedAreas
    ) {
        this.finalPathCoordinates = finalPathCoordinates;
        this.voiceInstructions = voiceInstructions;
        this.avoidedAreas = avoidedAreas;
    }

    // --- (오류의 원인: 이 메서드가 누락됨) ---
    /**
     * 도보 구간이 없거나 경로를 찾지 못한 경우 반환할 빈(Empty) 결과 객체
     */
    public static AvoidancePathResult empty() {
        return new AvoidancePathResult(
                Collections.emptyList(), // 빈 경로
                Collections.emptyList(), // 빈 음성 안내
                Collections.emptyList()  // 빈 회피 목록
        );
    }
}

