package Receiver.repositories;

import Receiver.entities.HourlyEnergyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HourlyEnergyConsumptionRepository extends JpaRepository<HourlyEnergyConsumption, Long> {
    List<HourlyEnergyConsumption> findByDeviceId(Long deviceId);
    HourlyEnergyConsumption findByDeviceIdAndTimestamp(Long deviceId, Long timestamp);

}
