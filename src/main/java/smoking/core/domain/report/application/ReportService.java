package smoking.core.domain.report.application;

import smoking.core.domain.report.dto.ReportRequestDTO;
import smoking.core.domain.report.dto.ReportResponseDTO;

public interface ReportService {

    ReportResponseDTO.CreateReportDTO createReport(ReportRequestDTO.CreateReportDTO createReportDto);
}
