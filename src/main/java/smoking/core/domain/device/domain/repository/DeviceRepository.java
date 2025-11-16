package smoking.core.domain.device.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smoking.core.domain.device.domain.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {
}
