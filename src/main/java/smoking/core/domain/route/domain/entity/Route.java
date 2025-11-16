package smoking.core.domain.route.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import smoking.core.domain.device.domain.entity.Device;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Route")
@Getter @Setter
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    // 경로 검색 기기 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "start_latitude", precision = 10, scale = 8, nullable = false)
    private BigDecimal startLatitude;

    @Column(name = "start_longitude", precision = 11, scale = 8, nullable = false)
    private BigDecimal startLongitude;

    @Column(name = "end_latitude", precision = 10, scale = 8, nullable = false)
    private BigDecimal endLatitude;

    @Column(name = "end_longitude", precision = 11, scale = 8, nullable = false)
    private BigDecimal endLongitude;

    @Column(name = "search_time", nullable = false, updatable = false)
    private LocalDateTime searchTime;

    // 이 경로가 회피한 흡연 구역 목록 (N:M의 1:N 부분)
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteSmokingArea> avoidedSmokingAreas;

    @PrePersist
    protected void onCreate() {
        this.searchTime = LocalDateTime.now();
    }
}
