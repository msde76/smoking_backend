package smoking.core.domain.report.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smoking.core.domain.report.application.ReportService;
import smoking.core.domain.report.dto.ReportRequestDTO;
import smoking.core.domain.report.dto.ReportResponseDTO;
import smoking.core.global.common.response.BaseResponse;
import smoking.core.global.error.code.status.SuccessStatus;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportRestController {

    public final ReportService reportService;

    @PostMapping()
    @Operation(summary = "신고 API", description = "사용자의 신고 내용을 받아 등록")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "REPORT_200", description = "OK, 성공적으로 신고되었습니다.")
    })
    public BaseResponse<ReportResponseDTO.CreateReportDTO> createReport(
            @RequestBody ReportRequestDTO.CreateReportDTO createReportDto
    ) {
        ReportResponseDTO.CreateReportDTO result = reportService.createReport(createReportDto);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }
}
