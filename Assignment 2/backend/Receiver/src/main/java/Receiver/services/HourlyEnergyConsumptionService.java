package Receiver.services;

import Receiver.entities.HourlyEnergyConsumption;
import Receiver.repositories.HourlyEnergyConsumptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
