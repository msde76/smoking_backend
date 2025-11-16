package smoking.core.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
