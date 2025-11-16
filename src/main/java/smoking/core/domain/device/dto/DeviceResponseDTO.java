package smoking.core.domain.device.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class DeviceResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetDeviceDto {

        private String deviceId;

        private LocalDateTime registrationDate;

        // tts_preference 컬럼이 JSON 타입이므로,
        // DB에서 String으로 받은 후 그대로 반환하거나
        // 별도의 DTO로 파싱하여 반환할 수 있습니다. (여기서는 String으로 가정)
        private String ttsPreference;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetVoiceDto {

        private String deviceId;

        // 변경된 설정 값을 확인시켜주기 위한 필드
        private Double speed;
        private String voice;
    }
}
