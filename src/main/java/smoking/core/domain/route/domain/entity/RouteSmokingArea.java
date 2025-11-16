package smoking.core.domain.route.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import smoking.core.domain.smoking.domain.entity.SmokingArea;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "RouteSmokingArea", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"route_id", "area_id"})
})
public class RouteSmokingArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_area_id")
    private Long id;

    // N:1 관계 (Route)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    // N:1 관계 (SmokingArea)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private SmokingArea smokingArea;

    // Getters and Setters
}