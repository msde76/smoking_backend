package smoking.core.domain.smoking.converter;

import smoking.core.domain.smoking.domain.entity.SmokingArea;
import smoking.core.domain.smoking.dto.SmokingResponseDTO;

public class SmokingConverter {

    public static SmokingResponseDTO.GetSmokingAreasDto toGetSmokingAreasDto(SmokingArea entity) {

        return SmokingResponseDTO.GetSmokingAreasDto.builder()
                .areaId(entity.getAreaId())
                .latitude(entity.getLatitude().doubleValue()) // BigDecimal -> Double
                .longitude(entity.getLongitude().doubleValue()) // BigDecimal -> Double
                .type(entity.getType().name()) // ENUM -> String (e.g., "공공부스")
                .address(entity.getAddress())
                .build();
    }
}
