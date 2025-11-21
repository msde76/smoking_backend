package smoking.core.domain.map.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 로그 확인용 추가 추천
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import smoking.core.domain.map.DTO.ExternalRouteData;
import smoking.core.domain.map.DTO.GoogleDirectionsResponse;
import smoking.core.domain.map.exception.routeException;
import smoking.core.global.error.code.status.ErrorStatus;

import java.net.URI;

@Slf4j // 로그를 위해 추가
@Service
@RequiredArgsConstructor
public class GoogleMapApiServiceImpl implements ExternalMapApiService {

    private final RestTemplate restTemplate;

    @Value("${google.maps.api-key}")
    private String googleApiKey;

    @Value("${google.maps.directions-url}")
    private String googleDirectionsUrl;

    @Override
    public ExternalRouteData fetchBasicRoute(
            Double startLatitude, Double startLongitude,
            Double endLatitude, Double endLongitude
    ) {

        String origin = startLatitude + "," + startLongitude;
        String destination = endLatitude + "," + endLongitude;

        // [수정됨] 한글이나 특수문자가 있어도 안전하게 처리하는 방식
        URI uri = UriComponentsBuilder.fromHttpUrl(googleDirectionsUrl)
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("mode", "transit")
                .queryParam("alternatives", "true")
                .queryParam("key", googleApiKey)
                .queryParam("language", "ko")
                .encode() // [중요] 1. 인코딩을 수행하라고 지시
                .build()  // [중요] 2. (true) 제거 -> 자동으로 인코딩 적용됨
                .toUri();

        log.info(">>> [Google Map Request] 요청 URL: {}", uri); // 요청 URL 확인용 로그

        ResponseEntity<GoogleDirectionsResponse> response = restTemplate.getForEntity(
                uri,
                GoogleDirectionsResponse.class
        );

        return convertToExternalRouteData(response.getBody());
    }

    private ExternalRouteData convertToExternalRouteData(GoogleDirectionsResponse response) {
        if (response == null || !response.getStatus().equals("OK") || response.getRoutes() == null || response.getRoutes().isEmpty()) {
            // Google API가 OK가 아닌 다른 상태(예: ZERO_RESULTS, OVER_QUERY_LIMIT 등)를 반환했을 때 로그를 남기면 디버깅에 좋습니다.
            log.warn(">>> [Google Map Error] Status: {}", response != null ? response.getStatus() : "NULL");
            throw new routeException(ErrorStatus._BAD_REQUEST);
        }

        return new ExternalRouteData(response);
    }
}