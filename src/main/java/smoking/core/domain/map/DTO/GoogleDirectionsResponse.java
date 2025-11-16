package smoking.core.domain.map.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleDirectionsResponse {

    private String status;
    private List<Route> routes;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        @JsonProperty("overview_polyline")
        private OverviewPolyline overviewPolyline; // 전체 경로 요약

        private List<Leg> legs; // 전체 경로의 상세 구간
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OverviewPolyline {
        @JsonProperty("points")
        private String points; // 압축된 좌표 문자열
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Leg {
        private List<Step> steps; // 각 구간별 상세 길 안내 (턴바이턴)
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Step {
        @JsonProperty("travel_mode")
        private String travelMode; // "WALKING" 또는 "TRANSIT"

        @JsonProperty("html_instructions")
        private String htmlInstructions; // 턴바이턴 안내 텍스트

        // 각 Step(구간)별 경로 좌표
        @JsonProperty("polyline")
        private OverviewPolyline polyline;

    }
}

