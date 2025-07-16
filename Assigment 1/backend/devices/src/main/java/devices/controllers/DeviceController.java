package devices.controllers;

import devices.dto.UserDTO;
import devices.dto.UserDeviceAssignmentRequest;
import devices.entities.Device;
import devices.entities.UserDevice;
import devices.services.DeviceService;
import devices.services.UserIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "http://localhost:3000")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserIntegrationService userIntegrationService;

    @PostMapping
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        Device newDevice = deviceService.createDevice(device);
        return ResponseEntity.ok(newDevice);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        return ResponseEntity.ok(device);
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device deviceDetails) {
        Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    public ResponseEntity<UserDevice> assignDeviceToUser(@RequestBody UserDeviceAssignmentRequest request) {
        UserDTO userDTO = userIntegrationService.getUserById(request.getUserId());

        if (userDTO == null) {
            return ResponseEntity.badRequest().body(null);
        }

        UserDevice userDevice = deviceService.assignDeviceToUser(request.getUserId(), request.getDeviceId());
        return ResponseEntity.ok(userDevice);
    }



    @GetMapping("/user/{userId}/devices")
    public ResponseEntity<List<UserDevice>> getDevicesForUser(@PathVariable Long userId) {
        List<UserDevice> userDevices = deviceService.getDevicesForUser(userId);
        if (userDevices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userDevices); 
    }


    @DeleteMapping("/unassign/{userId}/{deviceId}")
    public ResponseEntity<Void> unassignDevice(@PathVariable Long userId, @PathVariable Long deviceId) {
        try {
            deviceService.unassignDeviceFromUser(userId, deviceId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
