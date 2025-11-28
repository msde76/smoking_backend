package smoking.core.domain.nlu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NluResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true) // Gemini가 스키마 외의 값을 줘도 무시
    public static class ParseDto {

        private String intent; // 예: "SEARCH_ROUTE", "REPORT_SMOKING", "UNKNOWN"

        private String destination; // 예: "강남역"

        private String reportContent; // 예: "여기서 담배 피워요"

        private String controlAction; // 예: "상승", "하락", "초기화"
    }
}