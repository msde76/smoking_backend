package smoking.core.domain.map.application;

import lombok.RequiredArgsConstructor;
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

        URI uri = UriComponentsBuilder.fromHttpUrl(googleDirectionsUrl)
                .queryParam("origin", origin)
                .queryParam("destination", destination)
                .queryParam("mode", "transit") // 1. (수정) "walking" -> "transit"
                .queryParam("alternatives", "true")
                .queryParam("key", googleApiKey)
                .queryParam("language", "ko")
                .build(true)
                .toUri();

        ResponseEntity<GoogleDirectionsResponse> response = restTemplate.getForEntity(
                uri,
                GoogleDirectionsResponse.class
        );

        return convertToExternalRouteData(response.getBody());
    }

    private ExternalRouteData convertToExternalRouteData(GoogleDirectionsResponse response) {

        if (response == null || !response.getStatus().equals("OK") || response.getRoutes() == null || response.getRoutes().isEmpty()) {
            throw new routeException(ErrorStatus._BAD_REQUEST);
        }

        return new ExternalRouteData(response);
    }
}