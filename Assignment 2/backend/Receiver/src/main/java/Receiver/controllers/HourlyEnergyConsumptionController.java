package Receiver.controllers;

import Receiver.entities.HourlyEnergyConsumption;
import Receiver.services.HourlyEnergyConsumptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(
        origins = "http://frontend.localhost"
)
@RequestMapping("/api/consumptions")
public class HourlyEnergyConsumptionController {

    @Autowired
    private HourlyEnergyConsumptionService hourlyEnergyConsumptionService;

    @PostMapping
    public ResponseEntity<HourlyEnergyConsumption> saveConsumption(@RequestBody HourlyEnergyConsumption consumption) {
        HourlyEnergyConsumption savedConsumption = hourlyEnergyConsumptionService.saveConsumption(consumption);
        return ResponseEntity.ok(savedConsumption);
    }

    @GetMapping
    public ResponseEntity<List<HourlyEnergyConsumption>> getAllConsumptions() {
        List<HourlyEnergyConsumption> consumptions = hourlyEnergyConsumptionService.getAllConsumptions();
        return ResponseEntity.ok(consumptions);
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<HourlyEnergyConsumption>> getConsumptionsByDeviceId(@PathVariable Long deviceId) {
        List<HourlyEnergyConsumption> consumptions = hourlyEnergyConsumptionService.getConsumptionsByDeviceId(deviceId);
        return ResponseEntity.ok(consumptions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsumptionById(@PathVariable Long id) {
        hourlyEnergyConsumptionService.deleteConsumptionById(id);
        return ResponseEntity.noContent().build();
    }
}
