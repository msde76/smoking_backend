package smoking.core.domain.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class ReportRequestDTO {

    @Getter
    @NoArgsConstructor
    public static class CreateReportDTO {

        @NotBlank(message = "기기 ID는 필수입니다.")
        private String deviceId; // 신고한 기기의 ID

        @NotNull(message = "신고 위도는 필수입니다.")
        private BigDecimal reportedLatitude; // 신고된 위치 (위도)

        @NotNull(message = "신고 경도는 필수입니다.")
        private BigDecimal reportedLongitude; // 신고된 위치 (경도)

        @NotBlank(message = "신고 내용은 필수입니다.")
        private String description; // 신고 내용

        // 기존 흡연구역에 대한 민원일 경우 (신규 민원이면 null)
        private Long areaId;
    }
}
