package smoking.core.domain.device.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smoking.core.domain.device.converter.DeviceConverter;
import smoking.core.domain.device.domain.entity.Device;
import smoking.core.domain.device.domain.repository.DeviceRepository;
import smoking.core.domain.device.dto.DeviceRequestDTO;
import smoking.core.domain.device.dto.DeviceResponseDTO;
import smoking.core.domain.device.exception.deviceException;
import smoking.core.global.error.code.status.ErrorStatus;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceServiceImpl implements DeviceService{

    private final DeviceRepository deviceRepository;
    private final DeviceConverter deviceConverter;

    @Override
    @Transactional
    public DeviceResponseDTO.SetDeviceDto setDevice(String deviceId) {

        // 1. deviceId로 기기를 조회합니다.
        Optional<Device> optDevice = deviceRepository.findById(deviceId);

        Device device;
        if (optDevice.isPresent()) {
            // 2. 이미 등록된 기기인 경우, 해당 기기 정보를 가져옵니다.
            device = optDevice.get();
        } else {
            // 3. 등록되지 않은 기기인 경우, 컨버터를 통해 새 엔티티를 생성하고 저장(등록)합니다.
            Device newDevice = DeviceConverter.toDevice(deviceId);
            device = deviceRepository.save(newDevice);
        }

        return DeviceConverter.toDeviceDto(device);
    }

    @Override
    @Transactional
    public DeviceResponseDTO.SetVoiceDto setVoice(String deviceId, DeviceRequestDTO.SetVoiceDto requestDto) {

        // 1. 기기를 조회합니다. (없으면 예외 발생)
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new deviceException(ErrorStatus._UNAUTHORIZED));

        // 2. 요청 DTO를 JSON 문자열로 변환합니다. (Converter 활용)
        String newTtsPreference = deviceConverter.requestDtoToJsonString(requestDto);

        // 3. 엔티티의 ttsPreference 필드를 업데이트합니다.
        device.setTtsPreference(newTtsPreference);

        // 4. 변경된 엔티티를 DTO로 변환하여 반환합니다. (Converter 활용)
        return deviceConverter.toSetVoiceResponseDto(device);
    }
}
