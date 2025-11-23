package smoking.core.domain.report.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smoking.core.domain.device.domain.entity.Device;
import smoking.core.domain.device.domain.repository.DeviceRepository;
import smoking.core.domain.report.converter.ReportConverter;
import smoking.core.domain.report.domain.entity.UserReport;
import smoking.core.domain.report.domain.repository.UserReportRepository;
import smoking.core.domain.report.dto.ReportRequestDTO;
import smoking.core.domain.report.dto.ReportResponseDTO;
import smoking.core.domain.report.exception.reportException;
import smoking.core.domain.smoking.domain.entity.SmokingArea;
import smoking.core.domain.smoking.domain.repository.SmokingAreaRepository;
import smoking.core.global.error.code.status.ErrorStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final UserReportRepository userReportRepository;
    private final DeviceRepository deviceRepository;
    private final SmokingAreaRepository smokingAreaRepository;
    private final ReportConverter reportConverter;

    @Transactional
    public ReportResponseDTO.CreateReportDTO createReport(
            ReportRequestDTO.CreateReportDTO createReportDto
    ) {

        // 1. 신고한 Device 엔티티 조회
        Device device = deviceRepository.findById(createReportDto.getDeviceId())
                .orElseThrow(() -> new reportException(ErrorStatus._UNAUTHORIZED));

        // 2. (선택적) 기존 흡연구역에 대한 신고인지 확인
        SmokingArea smokingArea = null;
        if (createReportDto.getAreaId() != null) {
            smokingArea = smokingAreaRepository.findById(createReportDto.getAreaId())
                    .orElse(null); // (찾을 수 없으면 null로 처리)
        }

        // 3. 컨버터를 통해 엔티티 생성
        UserReport newUserReport = reportConverter.toUserReport(createReportDto, device, smokingArea);

        // 4. DB에 저장
        UserReport savedReport = userReportRepository.save(newUserReport);

        // 5. 응답 DTO로 변환하여 반환
        return reportConverter.toCreateReportDTO(savedReport);
    }

    @Transactional(readOnly = true)
    public List<ReportResponseDTO.ReportDetailDTO> getReportsByDeviceId(String deviceId) {
        // 1. deviceId로 신고 내역 조회
        List<UserReport> reports = userReportRepository.findByDevice_DeviceId(deviceId);

        // 2. DTO로 변환하여 반환
        return reports.stream()
                .map(reportConverter::toReportDetailDTO)
                .collect(Collectors.toList());
    }
}
