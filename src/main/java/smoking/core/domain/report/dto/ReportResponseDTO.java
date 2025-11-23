package smoking.core.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReportResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReportDTO {

        private Long reportId; // 생성된 신고 ID (PK)
        private String deviceId; // 신고한 기기 ID
        private String status; // 처리 상태 (예: "대기")
        private LocalDateTime reportDate; // 신고 일시
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportDetailDTO {

        private Long reportId; // 신고 ID (PK)
        private String deviceId; // 신고한 기기 ID
        private String status; // 처리 상태 (예: "대기", "승인", "반려")
        private LocalDateTime reportDate; // 신고 일시
        private BigDecimal reportedLatitude; // 신고된 위치 (위도)
        private BigDecimal reportedLongitude; // 신고된 위치 (경도)
        private String description; // 신고 내용
        private Long areaId; // 관련 흡연구역 ID (없으면 null)
    }
}
