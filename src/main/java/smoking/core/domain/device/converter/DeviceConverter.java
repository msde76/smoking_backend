package smoking.core.domain.device.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smoking.core.domain.device.domain.entity.Device;
import smoking.core.domain.device.dto.DeviceRequestDTO;
import smoking.core.domain.device.dto.DeviceResponseDTO;

@Component
@RequiredArgsConstructor
public class DeviceConverter {

    private final ObjectMapper objectMapper;

    public static Device toDevice(String deviceId) {
        Device device = new Device();
        device.setDeviceId(deviceId);

        // 예시: 기본 TTS 설정 (JSON 문자열)
        device.setTtsPreference("{\"speed\": 1.0, \"voice\": \"female_calm\"}");

        return device;
    }

    public static DeviceResponseDTO.SetDeviceDto toDeviceDto(Device device) {
        return DeviceResponseDTO.SetDeviceDto.builder()
                .deviceId(device.getDeviceId())
                .registrationDate(device.getRegistrationDate())
                .ttsPreference(device.getTtsPreference())
                .build();
    }

    public String requestDtoToJsonString(DeviceRequestDTO.SetVoiceDto requestDto) {
        try {
            return objectMapper.writeValueAsString(requestDto);
        } catch (JsonProcessingException e) {
            // 실제로는 커스텀 예외를 발생시키는 것이 좋습니다.
            throw new RuntimeException("TTS Preference 직렬화 실패", e);
        }
    }

    public DeviceResponseDTO.SetVoiceDto toSetVoiceResponseDto(Device device) {

        DeviceRequestDTO.SetVoiceDto preferences;
        try {
            // DB의 ttsPreference(String)를 DTO 객체로 파싱
            preferences = objectMapper.readValue(device.getTtsPreference(), DeviceRequestDTO.SetVoiceDto.class);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            // JSON이 비어있거나(null) 형식이 안 맞을 경우
            preferences = new DeviceRequestDTO.SetVoiceDto(); // 빈 객체 (또는 기본값)
        }

        return DeviceResponseDTO.SetVoiceDto.builder()
                .deviceId(device.getDeviceId())
                .speed(preferences.getSpeed())
                .voice(preferences.getVoice())
                .build();
    }
}
