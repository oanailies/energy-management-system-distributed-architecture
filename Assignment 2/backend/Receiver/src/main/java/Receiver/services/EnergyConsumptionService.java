package Receiver.services;

import Receiver.entities.DeviceMeasurement;
import Receiver.entities.HourlyEnergyConsumption;
import Receiver.repositories.DeviceMeasurementRepository;
import Receiver.repositories.HourlyEnergyConsumptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnergyConsumptionService {

    private final DeviceMeasurementRepository measurementRepository;
    private final HourlyEnergyConsumptionRepository consumptionRepository;

    public EnergyConsumptionService(DeviceMeasurementRepository measurementRepository,
                                    HourlyEnergyConsumptionRepository consumptionRepository) {
        this.measurementRepository = measurementRepository;
        this.consumptionRepository = consumptionRepository;
    }


}
