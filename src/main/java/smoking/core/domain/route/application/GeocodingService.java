package smoking.core.domain.route.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import smoking.core.domain.route.dto.GoogleGeocodingResponse;

import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final RestTemplate restTemplate;

    @Value("${google.maps.api-key}")
    private String googleApiKey;

    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json";


    /**
     * 주소 텍스트를 위도/경도 좌표로 변환합니다.
     * @param address 주소 또는 장소 이름 (예: "서울시청")
     * @return 위도, 경도
     */
    public Optional<GoogleGeocodingResponse.Coordinates> getCoordinates(String address) {

        URI uri = UriComponentsBuilder.fromHttpUrl(GEOCODING_URL)
                .queryParam("address", address)
                .queryParam("key", googleApiKey)
                .queryParam("language", "ko")
                .build(true)
                .toUri();

        ResponseEntity<GoogleGeocodingResponse> response = restTemplate.getForEntity(
                uri,
                GoogleGeocodingResponse.class
        );

        GoogleGeocodingResponse geocodingResponse = response.getBody();

        if (geocodingResponse == null || !geocodingResponse.getStatus().equals("OK") || geocodingResponse.getResults().isEmpty()) {
            return Optional.empty();
        }

        // 첫 번째 결과를 반환 (가장 정확도가 높은 결과)
        return geocodingResponse.getResults().stream()
                .findFirst()
                .map(result -> result.getGeometry().getLocation());
    }
}