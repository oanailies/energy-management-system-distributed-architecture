package Receiver.services;

import Receiver.entities.DeviceMeasurement;
import Receiver.repositories.DeviceMeasurementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceMeasurementService {

    private final DeviceMeasurementRepository deviceMeasurementRepository;

    public DeviceMeasurementService(DeviceMeasurementRepository deviceMeasurementRepository) {
        this.deviceMeasurementRepository = deviceMeasurementRepository;
    }

    public DeviceMeasurement saveMeasurement(DeviceMeasurement measurement) {
        return deviceMeasurementRepository.save(measurement);
    }

    public List<DeviceMeasurement> getAllMeasurements() {
        return deviceMeasurementRepository.findAll();
    }

    public List<DeviceMeasurement> getMeasurementsByDeviceId(Long deviceId) {
        return deviceMeasurementRepository.findByDeviceId(deviceId);
    }

    public void deleteMeasurementById(Long id) {
        if (deviceMeasurementRepository.existsById(id)) {
            deviceMeasurementRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Measurement with ID " + id + " does not exist.");
        }
    }
}
