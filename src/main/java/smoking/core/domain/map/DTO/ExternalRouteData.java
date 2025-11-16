package smoking.core.domain.map.DTO;

import lombok.Getter;
import java.util.List;


@Getter
public class ExternalRouteData {

    // 1. Google API 응답 원본
    private final GoogleDirectionsResponse rawGoogleResponse;

    // 2. PathFinder가 사용할 도보 구간 좌표
    private final List<List<Double>> allPathCoordinates;

    // 3. PathFinder가 사용할 도보 구간 안내
    private final List<String> allInstructions;


    public ExternalRouteData(GoogleDirectionsResponse rawResponse) {
        this.rawGoogleResponse = rawResponse;
        this.allPathCoordinates = null;
        this.allInstructions = null;
    }

    public ExternalRouteData(List<List<Double>> pathCoordinates, List<String> instructions) {
        this.rawGoogleResponse = null;
        this.allPathCoordinates = pathCoordinates;
        this.allInstructions = instructions;
    }
}

