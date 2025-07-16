package devices.services;

import devices.entities.Device;
import devices.entities.UserDevice;
import devices.dto.UserDTO;
import devices.repositories.DeviceRepository;
import devices.repositories.UserDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @Autowired
    private UserIntegrationService userIntegrationService;

    public Device createDevice(Device device) {
        return deviceRepository.save(device);
    }

    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id).orElse(null);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device updateDevice(Long id, Device deviceDetails) {
        Device device = getDeviceById(id);
        if (device != null) {
            device.setName(deviceDetails.getName());
            device.setDescription(deviceDetails.getDescription());
            device.setAddress(deviceDetails.getAddress());
            device.setMaxHourlyConsumption(deviceDetails.getMaxHourlyConsumption());
            return deviceRepository.save(device);
        }
        return null;
    }

    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }

    public UserDevice assignDeviceToUser(Long userId, Long deviceId) {
        // Verificăm dacă utilizatorul există
        UserDTO userDTO = userIntegrationService.getUserById(userId);
        if (userDTO == null) {
            throw new RuntimeException("User not found");
        }

        // Verificăm dacă device-ul există
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        // Verificăm dacă dispozitivul este deja asociat unui utilizator
        boolean isDeviceAssigned = userDeviceRepository.existsByDeviceId(deviceId);
        if (isDeviceAssigned) {
            throw new RuntimeException("Device is already assigned to another user");
        }

        // Creăm o înregistrare în tabela user_device
        UserDevice userDevice = UserDevice.builder()
                .userId(userId)
                .deviceId(deviceId)
                .build();

        return userDeviceRepository.save(userDevice); // Salvăm legătura în baza de date
    }


    public List<UserDevice> getDevicesForUser(Long userId) {
        return userDeviceRepository.findByUserId(userId); // Returnează toate dispozitivele asociate unui utilizator
    }

    public boolean isDeviceAssigned(Long deviceId) {
        // Verificăm dacă există o asociere pentru dispozitivul respectiv
        return userDeviceRepository.findByDeviceId(deviceId).isPresent();
    }

    public List<Device> getDevicesByUserId(Long userId) {
        // Găsim toate înregistrările din tabela de legătură UserDevice care au userId-ul respectiv
        List<UserDevice> userDevices = userDeviceRepository.findByUserId(userId);

        // Extragem toate device-urile asociate acestui userId
        List<Device> devices = new ArrayList<>();
        if (!userDevices.isEmpty()) {
            for (UserDevice userDevice : userDevices) {
                Device device = deviceRepository.findById(userDevice.getDeviceId()).orElse(null);
                if (device != null) {
                    devices.add(device);
                } else {
                    // În cazul în care dispozitivul nu a fost găsit, aruncăm o excepție
                    throw new RuntimeException("Device with ID " + userDevice.getDeviceId() + " not found");
                }
            }
        }
        return devices;
    }

    public UserDevice unassignDeviceFromUser(Long userId, Long deviceId) {
        // Verificăm dacă utilizatorul există
        UserDTO userDTO = userIntegrationService.getUserById(userId);
        if (userDTO == null) {
            throw new RuntimeException("User not found");
        }

        // Verificăm dacă dispozitivul există
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        // Verificăm dacă există asocierea între utilizator și dispozitiv
        Optional<UserDevice> userDeviceOpt = userDeviceRepository.findByUserIdAndDeviceId(userId, deviceId);

        if (!userDeviceOpt.isPresent()) {
            throw new RuntimeException("Device is not assigned to this user");
        }

        // Dacă asocierea există, o ștergem
        UserDevice userDevice = userDeviceOpt.get();
        userDeviceRepository.delete(userDevice); // Șterge asocierea

        return userDevice; // Returnăm obiectul UserDevice care a fost deconectat
    }




}
