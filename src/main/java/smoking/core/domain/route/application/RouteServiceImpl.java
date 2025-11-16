package smoking.core.domain.route.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smoking.core.domain.device.domain.entity.Device;
import smoking.core.domain.device.domain.repository.DeviceRepository;
import smoking.core.domain.map.DTO.ExternalRouteData;
import smoking.core.domain.map.DTO.GoogleDirectionsResponse;
import smoking.core.domain.map.application.ExternalMapApiService;
import smoking.core.domain.route.converter.RouteConverter;
import smoking.core.domain.route.domain.entity.Route;
import smoking.core.domain.route.domain.entity.RouteSmokingArea;
import smoking.core.domain.route.domain.repository.RouteRepository;
import smoking.core.domain.route.dto.GoogleGeocodingResponse;
import smoking.core.domain.route.dto.RouteRequestDTO;
import smoking.core.domain.route.dto.RouteResponseDTO;
import smoking.core.domain.route.path.AvoidancePathResult;
import smoking.core.domain.route.path.PathFinder; // (PathFinder의 '탐지' 기능만 사용)
import smoking.core.domain.smoking.domain.entity.AreaStatus;
import smoking.core.domain.smoking.domain.entity.SmokingArea;
import smoking.core.domain.smoking.domain.repository.SmokingAreaRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteServiceImpl implements RouteService {

    private final ExternalMapApiService mapApiService;
    private final DeviceRepository deviceRepository;
    private final RouteRepository routeRepository;
    private final SmokingAreaRepository smokingAreaRepository;
    private final RouteConverter routeConverter;
    private final PathFinder pathFinder; // (A* 대신 '탐지' 헬퍼로 사용)
    private final GeocodingService geocodingService;

    // (수정) "탐지" 로직을 위한 상수
    private static final int SMOKING_AREA_PENALTY_SCORE = 1000; // (흡연구역 1개당 페널티 점수)
    private static final double SMOKING_AREA_RADIUS_METERS = 30.0; // (PathFinder와 동일한 값 사용)

    @Override
    @Transactional
    public RouteResponseDTO.AvoidanceRouteDto findRouteByAddress(
            RouteRequestDTO.RouteByAddressDto requestDto
    ) {
        // 1. Geocoding: 목적지 주소(텍스트)를 좌표로 변환
        GoogleGeocodingResponse.Coordinates endCoords = geocodingService.getCoordinates(requestDto.getEndAddress())
                .orElseThrow(() -> new RuntimeException("목적지 주소를 좌표로 변환할 수 없습니다.")); // (커스텀 예외 권장)

        // 2. 기존 좌표 기반 DTO(AvoidanceRouteDto)를 생성
        RouteRequestDTO.AvoidanceRouteDto avoidanceDto = new RouteRequestDTO.AvoidanceRouteDto(
                requestDto.getDeviceId(),
                requestDto.getStartLatitude(),
                requestDto.getStartLongitude(),
                endCoords.getLatitude(),
                endCoords.getLongitude()
        );

        // 3. 기존 회피 경로 탐색 로직(findAvoidanceRoute)을 호출
        return findAvoidanceRoute(avoidanceDto);
    }

    @Override
    @Transactional
    public RouteResponseDTO.AvoidanceRouteDto findAvoidanceRoute(
            RouteRequestDTO.AvoidanceRouteDto requestDto
    ) {

        // 1. Device 조회
        Device device = deviceRepository.findById(requestDto.getDeviceId())
                .orElseThrow(() -> new RuntimeException("Device not found.")); // (커스텀 예외로 변경 권장)

        // 2. Google API로 '대안 경로(Alternatives)'가 포함된 경로 조회
        ExternalRouteData routeData = mapApiService.fetchBasicRoute(
                requestDto.getStartLatitude(), requestDto.getStartLongitude(),
                requestDto.getEndLatitude(), requestDto.getEndLongitude()
        );

        // 3. (수정) 최적의 "회피 경로"를 찾기 위한 변수 초기화
        GoogleDirectionsResponse.Route bestRoute = null; // 가장 쾌적한(흡연구역 없는) 경로
        List<SmokingArea> bestDetectedAreas = new ArrayList<>(); // 해당 경로에서 탐지된 흡연구역
        int minScore = Integer.MAX_VALUE; // 최소 페널티 점수

        // 4. (수정) DB에서 모든 '승인' 상태의 흡연구역 조회
        List<SmokingArea> allSmokingAreas = smokingAreaRepository.findAllByStatus(AreaStatus.승인);

        // 5. (수정) Google이 준 *모든 대안 경로(routes)*를 순회
        if (routeData.getRawGoogleResponse() == null || routeData.getRawGoogleResponse().getRoutes() == null) {
            throw new RuntimeException("Google API로부터 유효한 경로 응답을 받지 못했습니다.");
        }

        for (GoogleDirectionsResponse.Route currentRoute : routeData.getRawGoogleResponse().getRoutes()) {

            if (currentRoute.getLegs() == null || currentRoute.getLegs().isEmpty()) continue;

            // 6. 이 경로의 모든 '도보' 구간 좌표 추출
            List<List<Double>> walkingSegments = extractWalkingSegments(currentRoute.getLegs().get(0).getSteps());

            // 7. (PathFinder 탐지 로직 재활용) 이 도보 구간이 흡연구역을 몇 개 지나는지 '탐지'
            List<SmokingArea> detectedAreas;
            if (walkingSegments.isEmpty()) {
                detectedAreas = Collections.emptyList();
            } else {
                // (PathFinder의 findNearbyAreas 메서드를 사용하거나, 이 클래스 내부에 구현)
                detectedAreas = findNearbyAreas(walkingSegments, allSmokingAreas);
            }

            // 8. (수정) 경로 점수 계산 (흡연구역 개수만으로 점수 계산)
            int score = (detectedAreas.size() * SMOKING_AREA_PENALTY_SCORE);

            // 9. (수정) 이 경로가 현재까지의 최적 경로인지 비교
            if (score < minScore) {
                minScore = score;
                bestRoute = currentRoute;
                bestDetectedAreas = detectedAreas;
            }
        }

        // 10. (수정) 최종 선택된 '최적 경로(bestRoute)'가 없으면 예외 처리
        if (bestRoute == null) {
            throw new RuntimeException("유효한 경로를 찾지 못했습니다.");
        }

        // 11. 최종 경로의 좌표 및 음성 안내 조합
        List<List<Double>> finalCombinedPath = new ArrayList<>();
        List<AvoidancePathResult.Instruction> finalInstructions = new ArrayList<>();

        // (경고 메시지 추가)
        if (!bestDetectedAreas.isEmpty()) {
            String areaNames = bestDetectedAreas.stream()
                    .map(area -> area.getAddress() != null ? area.getAddress() : "등록된 구역")
                    .collect(Collectors.joining(", "));

            finalInstructions.add(new AvoidancePathResult.Instruction(
                    "경로 상 흡연구역 " + bestDetectedAreas.size() + "개(" + areaNames + ")가 탐지되었습니다.", 0.0, 0.0
            ));
        }

        // 최종 선택된 bestRoute의 Step들로 경로 재구성
        for (GoogleDirectionsResponse.Step step : bestRoute.getLegs().get(0).getSteps()) {
            List<List<Double>> segmentPath = decodePoly(step.getPolyline().getPoints());
            finalCombinedPath.addAll(segmentPath);

            finalInstructions.add(new AvoidancePathResult.Instruction(
                    step.getHtmlInstructions().replaceAll("<[^>]*>", ""), // HTML 태그 제거
                    segmentPath.isEmpty() ? 0.0 : segmentPath.get(0).get(0), // Lat
                    segmentPath.isEmpty() ? 0.0 : segmentPath.get(0).get(1)  // Lng
            ));
        }

        // 12. DB 저장
        Route routeToSave = routeConverter.toRoute(device, requestDto);
        if (!bestDetectedAreas.isEmpty()) {
            List<RouteSmokingArea> routeSmokingAreas = bestDetectedAreas.stream()
                    .map(area -> RouteSmokingArea.builder().route(routeToSave).smokingArea(area).build())
                    .collect(Collectors.toList());
            routeToSave.setAvoidedSmokingAreas(routeSmokingAreas);
        }
        Route savedRoute = routeRepository.save(routeToSave);

        // 13. 최종 결과 DTO 생성 및 반환
        AvoidancePathResult finalPathResult = new AvoidancePathResult(
                finalCombinedPath,
                finalInstructions,
                bestDetectedAreas
        );
        return routeConverter.toAvoidanceRouteDto(savedRoute, finalPathResult);
    }

    /**
     * (헬퍼) Google Step 리스트에서 'WALKING' 구간의 좌표만 모두 추출
     */
    private List<List<Double>> extractWalkingSegments(List<GoogleDirectionsResponse.Step> steps) {
        List<List<Double>> walkingPaths = new ArrayList<>();
        if (steps == null) return walkingPaths;

        for (GoogleDirectionsResponse.Step step : steps) {
            // (수정) .equals -> .equalsIgnoreCase (대소문자 구분 없이 'WALKING' 체크)
            if (step.getTravelMode() != null && step.getTravelMode().equalsIgnoreCase("WALKING")) {
                if (step.getPolyline() != null && step.getPolyline().getPoints() != null) {
                    walkingPaths.addAll(decodePoly(step.getPolyline().getPoints()));
                }
            }
        }
        return walkingPaths;
    }

    /**
     * (헬퍼) 특정 경로(좌표 리스트)가 흡연구역과 겹치는지 탐지
     * (PathFinder에서 이쪽으로 로직 이동)
     */
    public List<SmokingArea> findNearbyAreas(List<List<Double>> path, List<SmokingArea> allAreas) {
        Set<SmokingArea> nearbyAreas = new HashSet<>();
        if (path == null || allAreas == null || path.isEmpty() || allAreas.isEmpty()) {
            return Collections.emptyList();
        }

        for (List<Double> coords : path) {
            double lat = coords.get(0); // Google: Lat (위도)
            double lng = coords.get(1); // Google: Lng (경도)

            for (SmokingArea area : allAreas) {
                double dist = calculateHaversineDistance(
                        lat, lng,
                        area.getLatitude().doubleValue(), area.getLongitude().doubleValue()
                );
                if (dist <= SMOKING_AREA_RADIUS_METERS) {
                    nearbyAreas.add(area); // 겹치는 구역 추가
                }
            }
        }
        return new ArrayList<>(nearbyAreas);
    }


    /**
     * (헬퍼 메서드) Google Polyline 디코더
     */
    private List<List<Double>> decodePoly(String encoded) {
        List<List<Double>> poly = new ArrayList<>();
        if (encoded == null || encoded.isEmpty()) return poly;

        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                if (index >= len) break;
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                if (index >= len) break;
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            List<Double> p = new ArrayList<>();
            p.add(((double) lat / 1E5)); // 0: 위도(Lat)
            p.add(((double) lng / 1E5)); // 1: 경도(Lng)
            poly.add(p);
        }
        return poly;
    }

    /**
     * (헬퍼-유틸리티) Haversine 공식을 사용한 두 좌표 간 거리 계산 (미터 단위)
     */
    private static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반경 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000; // km를 미터로 변환
    }
}