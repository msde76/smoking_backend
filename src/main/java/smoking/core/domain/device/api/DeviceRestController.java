package smoking.core.domain.device.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smoking.core.domain.device.application.DeviceService;
import smoking.core.domain.device.dto.DeviceRequestDTO;
import smoking.core.domain.device.dto.DeviceResponseDTO;
import smoking.core.global.common.response.BaseResponse;
import smoking.core.global.error.code.status.SuccessStatus;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceRestController {

    private final DeviceService deviceService;

    @PostMapping("/init")
    @Operation(summary = "기기 초기화 및 설정 조회 API", description = "사용자가 처음 앱 사용 시 기기를 등록")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "DEVICE_200", description = "OK, 성공적으로 설정되었습니다.")
    })
    public BaseResponse<DeviceResponseDTO.SetDeviceDto> setDevice(
            @RequestParam String deviceId
    ) {
        DeviceResponseDTO.SetDeviceDto result = deviceService.setDevice(deviceId);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }

    @PatchMapping("/{device_id}/preferences")
    @Operation(summary = "음성 안내 설정 변경 API", description = "사용자가 기호에 맞게 음성 안내 설정 변경")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "DEVICE_200", description = "OK, 성공적으로 변경되었습니다.")
    })
    public BaseResponse<DeviceResponseDTO.SetVoiceDto> setVoice(
            @PathVariable (value = "device_id") String deviceId,
            @RequestBody DeviceRequestDTO.SetVoiceDto voiceDto
    ) {
        DeviceResponseDTO.SetVoiceDto result = deviceService.setVoice(deviceId, voiceDto);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }

}
