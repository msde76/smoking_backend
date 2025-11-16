package smoking.core.domain.device.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import smoking.core.domain.report.domain.entity.UserReport;
import smoking.core.domain.route.domain.entity.Route;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Device")
@Getter @Setter
public class Device {

    @Id
    @Column(name = "device_id", length = 255)
    private String deviceId; // 기기 고유 UUID

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    // JSON 타입의 TTS 설정을 문자열로 저장 (또는 @Convert 사용)
    @Column(name = "tts_preference", columnDefinition = "JSON")
    private String ttsPreference;

    // Device(1)가 Route(N)를 가짐 (읽기 전용, 관리 주체는 Route)
    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private List<Route> routes;

    // Device(1)가 UserReport(N)를 가짐 (읽기 전용, 관리 주체는 UserReport)
    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private List<UserReport> userReports;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDateTime.now();
    }
}
