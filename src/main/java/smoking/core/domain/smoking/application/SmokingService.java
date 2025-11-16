package smoking.core.domain.smoking.application;

import smoking.core.domain.smoking.dto.SmokingResponseDTO;

import java.util.List;

public interface SmokingService {

    List<SmokingResponseDTO.GetSmokingAreasDto> getSmokingAreas(
            Double minLat, Double maxLat, Double minLng, Double maxLng
    );
}
