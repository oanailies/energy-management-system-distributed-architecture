package Receiver.controllers;

import Receiver.entities.DeviceMeasurement;
import Receiver.services.DeviceMeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(
        origins = "http://frontend.localhost"
)
@RequestMapping("/api/measurements")
public class DeviceMeasurementController {

    @Autowired
    private DeviceMeasurementService deviceMeasurementService;

    @PostMapping
    public ResponseEntity<DeviceMeasurement> saveMeasurement(@RequestBody DeviceMeasurement measurement) {
        DeviceMeasurement savedMeasurement = deviceMeasurementService.saveMeasurement(measurement);
        return ResponseEntity.ok(savedMeasurement);
    }

    @GetMapping
    public ResponseEntity<List<DeviceMeasurement>> getAllMeasurements() {
        List<DeviceMeasurement> measurements = deviceMeasurementService.getAllMeasurements();
        return ResponseEntity.ok(measurements);
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<DeviceMeasurement>> getMeasurementsByDeviceId(@PathVariable Long deviceId) {
        List<DeviceMeasurement> measurements = deviceMeasurementService.getMeasurementsByDeviceId(deviceId);
        return ResponseEntity.ok(measurements);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeasurementById(@PathVariable Long id) {
        deviceMeasurementService.deleteMeasurementById(id);
        return ResponseEntity.noContent().build();
    }
}
