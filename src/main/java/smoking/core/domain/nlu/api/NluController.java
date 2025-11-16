package smoking.core.domain.nlu.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smoking.core.domain.nlu.application.NluService;
import smoking.core.domain.nlu.dto.NluRequestDTO;
import smoking.core.domain.nlu.dto.NluResponseDTO;
import smoking.core.global.common.response.BaseResponse;
import smoking.core.global.error.code.status.SuccessStatus;


@RestController
@RequiredArgsConstructor
@RequestMapping("/nlu")
public class NluController {

    private final NluService nluService;

    @PostMapping("/command")
    @Operation(summary = "음성 명령 분석 API", description = "Whisper AI 등으로 변환된 텍스트를 받아 NLU(자연어 이해) 수행")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NLU_200", description = "OK, 성공적으로 분석되었습니다.")
    })
    public BaseResponse<NluResponseDTO.ParseDto> parseCommand(
            @RequestBody NluRequestDTO.ParseDto requestDto
    ) {
        NluResponseDTO.ParseDto result = nluService.parseCommand(requestDto.getCommandText());
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }
}