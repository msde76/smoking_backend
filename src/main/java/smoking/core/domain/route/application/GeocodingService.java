package smoking.core.domain.route.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import smoking.core.domain.route.dto.GoogleGeocodingResponse;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final RestTemplate restTemplate;

    @Value("${google.maps.api-key}")
    private String googleApiKey;

    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public Optional<GoogleGeocodingResponse.Coordinates> getCoordinates(String address) {

        // [수정] UriComponentsBuilder를 사용하여 한글 주소를 안전하게 인코딩합니다.
        URI uri = UriComponentsBuilder.fromHttpUrl(GEOCODING_URL)
                .queryParam("address", address) // "강남역"이 들어와도 안전함
                .queryParam("key", googleApiKey)
                .queryParam("language", "ko")
                .encode() // [중요 1] URL 인코딩을 수행합니다.
                .build()  // [중요 2] build(true) 대신 build()를 사용합니다.
                .toUri();

        log.info(">>> [Geocoding Request] 요청 URL: {}", uri);

        try {
            ResponseEntity<GoogleGeocodingResponse> response = restTemplate.getForEntity(
                    uri,
                    GoogleGeocodingResponse.class
            );

            GoogleGeocodingResponse geocodingResponse = response.getBody();

            // 응답 상태가 OK가 아니거나 결과가 없으면 빈 값 반환
            if (geocodingResponse == null || !geocodingResponse.getStatus().equals("OK")) {
                log.warn(">>> [Geocoding Fail] 상태: {}", geocodingResponse != null ? geocodingResponse.getStatus() : "NULL");
                return Optional.empty();
            }

            if (geocodingResponse.getResults() == null || geocodingResponse.getResults().isEmpty()) {
                log.warn(">>> [Geocoding Fail] 검색 결과 없음");
                return Optional.empty();
            }

            // 첫 번째 결과를 반환 (가장 정확도가 높은 결과)
            return geocodingResponse.getResults().stream()
                    .findFirst()
                    .map(result -> result.getGeometry().getLocation());

        } catch (Exception e) {
            log.error(">>> [Geocoding Error] API 호출 중 오류 발생: {}", e.getMessage());
            return Optional.empty();
        }
    }
}