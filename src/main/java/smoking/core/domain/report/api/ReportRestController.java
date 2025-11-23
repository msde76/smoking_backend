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

import java.util.List;

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

    @GetMapping("/device/{deviceId}")
    @Operation(summary = "사용자 신고 내역 조회 API", description = "deviceId로 해당 사용자가 신고한 내역을 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "REPORT_200", description = "OK, 신고 내역 조회에 성공했습니다.")
    })
    public BaseResponse<List<ReportResponseDTO.ReportDetailDTO>> getReportsByDeviceId(
            @PathVariable String deviceId
    ) {
        List<ReportResponseDTO.ReportDetailDTO> result = reportService.getReportsByDeviceId(deviceId);
        return BaseResponse.onSuccess(SuccessStatus.OK, result);
    }

    @DeleteMapping("/{reportId}")
    @Operation(summary = "신고 삭제 API", description = "특정 신고 ID를 이용해 신고 내역을 삭제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse( responseCode = "REPORT_200", description = "OK, 성공적으로 신고가 삭제되었습니다.")
    })
    public BaseResponse<Void> deleteReport(
            @PathVariable Long reportId
    ) {
        reportService.deleteReport(reportId);
        return BaseResponse.onSuccess(SuccessStatus.OK, null);
    }
}
