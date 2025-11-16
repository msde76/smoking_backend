package smoking.core.domain.device.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class DeviceRequestDTO {

    @Getter
    @NoArgsConstructor
    public static class SetVoiceDto {

        // 예시: tts_preference 컬럼이 {"speed": 1.2, "voice": "female_calm"} 형태일 경우
        private Double speed;
        private String voice;
    }
}
