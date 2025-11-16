package smoking.core.domain.route.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RouteRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvoidanceRouteDto {

        @NotNull
        private String deviceId; // ERD 기반 필수 FK

        @NotNull
        private Double startLatitude;
        @NotNull
        private Double startLongitude;

        @NotNull
        private Double endLatitude;
        @NotNull
        private Double endLongitude;
    }

    @Getter
    @NoArgsConstructor
    public static class RouteByAddressDto {

        @NotBlank
        private String deviceId;

        // 시작 좌표는 프론트에서 GPS로 얻어와서 보냄 (현재 위치)
        @NotNull
        private Double startLatitude;
        @NotNull
        private Double startLongitude;

        // 도착지 주소는 텍스트로 보냄
        @NotBlank
        private String endAddress;
    }
}
