package smoking.core.domain.smoking.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smoking.core.domain.smoking.converter.SmokingConverter;
import smoking.core.domain.smoking.domain.entity.AreaStatus;
import smoking.core.domain.smoking.domain.entity.SmokingArea;
import smoking.core.domain.smoking.domain.repository.SmokingAreaRepository;
import smoking.core.domain.smoking.dto.SmokingResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SmokingServiceImpl implements SmokingService {

    private final SmokingAreaRepository smokingAreaRepository;

    @Override
    public List<SmokingResponseDTO.GetSmokingAreasDto> getSmokingAreas(
            Double minLat, Double maxLat, Double minLng, Double maxLng
    ) {
        // 1. DTO가 아닌 파라미터를 BigDecimal로 변환
        BigDecimal minLatBd = BigDecimal.valueOf(minLat);
        BigDecimal maxLatBd = BigDecimal.valueOf(maxLat);
        BigDecimal minLngBd = BigDecimal.valueOf(minLng);
        BigDecimal maxLngBd = BigDecimal.valueOf(maxLng);

        // 2. Repository 호출
        List<SmokingArea> areas = smokingAreaRepository
                .findAllByLatitudeBetweenAndLongitudeBetweenAndStatus(
                        minLatBd, maxLatBd, minLngBd, maxLngBd, AreaStatus.승인
                );

        // 3. DTO 리스트로 변환 후 반환
        return areas.stream()
                .map(SmokingConverter::toGetSmokingAreasDto)
                .collect(Collectors.toList());
    }

}
