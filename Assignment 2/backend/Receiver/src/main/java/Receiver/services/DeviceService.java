package Receiver.services;

import Receiver.entities.Device;
import Receiver.repositories.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public void saveDevice(Device device) {
        if (deviceRepository.existsById(device.getId())) {
            Device existingDevice = deviceRepository.findById(device.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Device with ID " + device.getId() + " not found."));
            existingDevice.setName(device.getName());
            existingDevice.setDescription(device.getDescription());
            existingDevice.setAddress(device.getAddress());
            existingDevice.setMaxHourlyConsumption(device.getMaxHourlyConsumption());
            deviceRepository.save(existingDevice);
        } else {
            deviceRepository.save(device);
        }
    }

    public void deleteDeviceById(Long id) {
        if (deviceRepository.existsById(id)) {
            deviceRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Device with ID " + id + " does not exist.");
        }
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Device with ID " + id + " not found."));
    }
}
