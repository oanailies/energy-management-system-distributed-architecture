package Receiver.services;

import Receiver.entities.HourlyEnergyConsumption;
import Receiver.repositories.HourlyEnergyConsumptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class HourlyEnergyConsumptionService {

    private final HourlyEnergyConsumptionRepository hourlyEnergyConsumptionRepository;

    public HourlyEnergyConsumptionService(HourlyEnergyConsumptionRepository hourlyEnergyConsumptionRepository) {
        this.hourlyEnergyConsumptionRepository = hourlyEnergyConsumptionRepository;
    }

    public HourlyEnergyConsumption saveConsumption(HourlyEnergyConsumption consumption) {
        return hourlyEnergyConsumptionRepository.save(consumption);
    }

    public List<HourlyEnergyConsumption> getAllConsumptions() {
        return hourlyEnergyConsumptionRepository.findAll();
    }

    public List<HourlyEnergyConsumption> getConsumptionsByDeviceId(Long deviceId) {
        return hourlyEnergyConsumptionRepository.findByDeviceId(deviceId);
    }

    public void deleteConsumptionById(Long id) {
        if (hourlyEnergyConsumptionRepository.existsById(id)) {
            hourlyEnergyConsumptionRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Consumption with ID " + id + " does not exist.");
        }
    }

    public List<HourlyEnergyConsumption> getConsumptionsByDeviceAndDate(Long deviceId, LocalDate date) {
        return hourlyEnergyConsumptionRepository.findByDeviceIdAndDate(deviceId, date);
    }

    public List<Map<String, Object>> getAggregatedHourlyConsumption(Long deviceId, LocalDate date) {
        List<HourlyEnergyConsumption> consumptions = hourlyEnergyConsumptionRepository.findByDeviceIdAndDate(deviceId, date);

        Map<Integer, Double> hourlyData = new TreeMap<>();
        for (HourlyEnergyConsumption consumption : consumptions) {
            ZonedDateTime dateTime = Instant.ofEpochMilli(consumption.getTimestamp())
                    .atZone(ZoneId.systemDefault());
            int hour = dateTime.getHour();
            hourlyData.put(hour, hourlyData.getOrDefault(hour, 0.0) + consumption.getTotalConsumption());
        }

        for (int i = 0; i < 24; i++) {
            hourlyData.putIfAbsent(i, 0.0);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            Map<String, Object> hourData = new HashMap<>();
            hourData.put("hour", hour);
            hourData.put("totalConsumption", hourlyData.get(hour));
            result.add(hourData);
        }

        return result;
    }


}
