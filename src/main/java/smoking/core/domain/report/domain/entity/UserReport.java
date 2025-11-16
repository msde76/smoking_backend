package smoking.core.domain.report.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import smoking.core.domain.device.domain.entity.Device;
import smoking.core.domain.smoking.domain.entity.SmokingArea;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "UserReport")
@Getter @Setter
public class UserReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    // 신고자 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    // 관련 흡연 구역 (N:1 관계, Null 가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private SmokingArea smokingArea;

    @Column(name = "reported_latitude", precision = 10, scale = 8)
    private BigDecimal reportedLatitude;

    @Column(name = "reported_longitude", precision = 11, scale = 8)
    private BigDecimal reportedLongitude;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "report_date", nullable = false, updatable = false)
    private LocalDateTime reportDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status; // ENUM: 대기, 승인, 반려

    @PrePersist
    protected void onCreate() {
        this.reportDate = LocalDateTime.now();
        this.status = ReportStatus.대기;
    }
}
