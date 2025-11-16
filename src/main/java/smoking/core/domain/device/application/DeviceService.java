package smoking.core.domain.device.application;

import smoking.core.domain.device.dto.DeviceRequestDTO;
import smoking.core.domain.device.dto.DeviceResponseDTO;

import java.util.List;

public interface DeviceService {

    DeviceResponseDTO.SetDeviceDto setDevice(String deviceId);

    DeviceResponseDTO.SetVoiceDto setVoice(String deviceId, DeviceRequestDTO.SetVoiceDto requestDto);
}
