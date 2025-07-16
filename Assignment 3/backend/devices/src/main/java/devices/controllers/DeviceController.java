package devices.controllers;

import devices.dto.UserDTO;
import devices.dto.UserDeviceAssignmentRequest;
import devices.entities.Device;
import devices.entities.UserDevice;
import devices.services.DeviceService;
import devices.services.UserIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(
        origins = "http://frontend.localhost"
)
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserIntegrationService userIntegrationService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        Device newDevice = deviceService.createDevice(device);
        return ResponseEntity.ok(newDevice);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device deviceDetails) {
        Device updatedDevice = deviceService.updateDevice(id, deviceDetails);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDevice> assignDeviceToUser(@RequestBody UserDeviceAssignmentRequest request) {
        UserDTO userDTO = userIntegrationService.getUserById(request.getUserId());

        if (userDTO == null) {
            return ResponseEntity.badRequest().body(null);
        }

        UserDevice userDevice = deviceService.assignDeviceToUser(request.getUserId(), request.getDeviceId());
        return ResponseEntity.ok(userDevice);
    }

    @DeleteMapping("/unassign/{userId}/{deviceId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> unassignDevice(@PathVariable Long userId, @PathVariable Long deviceId) {
        try {
            deviceService.unassignDeviceFromUser(userId, deviceId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("authenticated")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceService.getDeviceById(id);
        return ResponseEntity.ok(device);
    }

    @GetMapping
    @PreAuthorize("authenticated")
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/user/{userId}/devices")
    @PreAuthorize("authenticated")
    public ResponseEntity<List<UserDevice>> getDevicesForUser(@PathVariable Long userId) {
        List<UserDevice> userDevices = deviceService.getDevicesForUser(userId);
        if (userDevices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userDevices);
    }
}
