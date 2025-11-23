package smoking.core.domain.report.application;

import smoking.core.domain.report.dto.ReportRequestDTO;
import smoking.core.domain.report.dto.ReportResponseDTO;

import java.util.List;

public interface ReportService {

    ReportResponseDTO.CreateReportDTO createReport(ReportRequestDTO.CreateReportDTO createReportDto);

    List<ReportResponseDTO.ReportDetailDTO> getReportsByDeviceId(String deviceId);
}
