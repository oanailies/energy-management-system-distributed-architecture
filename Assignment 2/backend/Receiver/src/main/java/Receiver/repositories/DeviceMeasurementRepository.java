package Receiver.repositories;

import Receiver.entities.DeviceMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceMeasurementRepository extends JpaRepository<DeviceMeasurement, Long> {
    List<DeviceMeasurement> findByDeviceId(Long deviceId);

    List<DeviceMeasurement> findByDeviceIdAndTimestampBetween(Long deviceId, Long startTimestamp, Long endTimestamp);

    Optional<Object> findTopByDeviceIdOrderByTimestampDesc(Long deviceId);
}
