package smoking.core.domain.smoking.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smoking.core.domain.smoking.application.SmokingService;
import smoking.core.domain.smoking.dto.SmokingResponseDTO;
import smoking.core.global.common.response.BaseResponse;
import smoking.core.global.error.code.status.SuccessStatus;

import java.util.List;

@RestController
@RequestMapping("/smoking")
@RequiredArgsConstructor
public class SmokingRestController {

    public final SmokingService smokingService;

    @GetMapping("/areas")
    @Operation(summary = "주변 흡연 구역 조회 API", description = "지도 경계 내의 승인된 흡연 구역 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SMOKING_200", description = "OK, 성공적으로 조회되었습니다.")
    })
    public BaseResponse<List<SmokingResponseDTO.GetSmokingAreasDto>> getSmokingAreas(
            @RequestParam Double min_lat,
            @RequestParam Double max_lat,
            @RequestParam Double min_lng,
            @RequestParam Double max_lng
    ) {
        List<SmokingResponseDTO.GetSmokingAreasDto> result = smokingService.getSmokingAreas(
                min_lat, max_lat, min_lng, max_lng
        );
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }
}
