package smoking.core.domain.report.converter;

import org.springframework.stereotype.Component;
import smoking.core.domain.device.domain.entity.Device;
import smoking.core.domain.report.domain.entity.ReportStatus;
import smoking.core.domain.report.domain.entity.UserReport;
import smoking.core.domain.report.dto.ReportRequestDTO;
import smoking.core.domain.report.dto.ReportResponseDTO;
import smoking.core.domain.smoking.domain.entity.SmokingArea;

import java.time.LocalDateTime;

@Component
public class ReportConverter {

    public UserReport toUserReport(
            ReportRequestDTO.CreateReportDTO requestDto,
            Device device,
            SmokingArea smokingArea
    ) {

        return UserReport.builder()
                .device(device)
                .smokingArea(smokingArea) // (기존 구역 신고 시)
                .reportedLatitude(requestDto.getReportedLatitude())
                .reportedLongitude(requestDto.getReportedLongitude())
                .description(requestDto.getDescription())
                .reportDate(LocalDateTime.now())
                .status(ReportStatus.대기) // 신고 접수 시 기본 상태
                .build();
    }

    public ReportResponseDTO.CreateReportDTO toCreateReportDTO(UserReport entity) {
        return ReportResponseDTO.CreateReportDTO.builder()
                .reportId(entity.getReportId())
                .deviceId(entity.getDevice().getDeviceId())
                .status(entity.getStatus().name())
                .reportDate(entity.getReportDate())
                .build();
    }

    public ReportResponseDTO.ReportDetailDTO toReportDetailDTO(UserReport entity) {
        return ReportResponseDTO.ReportDetailDTO.builder()
                .reportId(entity.getReportId())
                .deviceId(entity.getDevice().getDeviceId())
                .status(entity.getStatus().name())
                .reportDate(entity.getReportDate())
                .reportedLatitude(entity.getReportedLatitude())
                .reportedLongitude(entity.getReportedLongitude())
                .description(entity.getDescription())
                .areaId(entity.getSmokingArea() != null ? entity.getSmokingArea().getAreaId() : null)
                .build();
    }
}
