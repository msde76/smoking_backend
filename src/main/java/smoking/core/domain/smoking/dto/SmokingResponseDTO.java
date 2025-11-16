package smoking.core.domain.smoking.dto;

import lombok.Builder;
import lombok.Getter;

public class SmokingResponseDTO {

    @Builder
    @Getter
    public static class GetSmokingAreasDto {

        private Long areaId;
        private Double latitude;
        private Double longitude;
        private String type; // ENUM을 String으로 변환
        private String address;
    }
}
