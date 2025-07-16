package Receiver.repositories;

import Receiver.entities.HourlyEnergyConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HourlyEnergyConsumptionRepository extends JpaRepository<HourlyEnergyConsumption, Long> {
    List<HourlyEnergyConsumption> findByDeviceId(Long deviceId);
    HourlyEnergyConsumption findByDeviceIdAndTimestamp(Long deviceId, Long timestamp);

    @Query("SELECT h FROM HourlyEnergyConsumption h WHERE h.deviceId = :deviceId AND h.date = :date")
    List<HourlyEnergyConsumption> findByDeviceIdAndDate(
            @Param("deviceId") Long deviceId,
            @Param("date") LocalDate date
    );


}
