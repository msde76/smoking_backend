package smoking.core.domain.nlu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class NluRequestDTO {

    @Getter
    @NoArgsConstructor
    public static class ParseDto {
        private String commandText; // (예: "강남역으로 가줘")
    }
}