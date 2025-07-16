package Receiver.controllers;

import Receiver.entities.HourlyEnergyConsumption;
import Receiver.services.HourlyEnergyConsumptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consumptions")
@CrossOrigin(origins = "http://frontend.localhost")
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

    //7-9
    @GetMapping("/device/{deviceId}/date")
    public ResponseEntity<List<HourlyEnergyConsumption>> getConsumptionsByDeviceAndDate(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<HourlyEnergyConsumption> consumptions = hourlyEnergyConsumptionService.getConsumptionsByDeviceAndDate(deviceId, date);
        return ResponseEntity.ok(consumptions);
    }

    @GetMapping("/device/{deviceId}/date/aggregate")
    public ResponseEntity<List<Map<String, Object>>> getHourlyAggregatedConsumption(
            @PathVariable Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Map<String, Object>> aggregatedData = hourlyEnergyConsumptionService.getAggregatedHourlyConsumption(deviceId, date);
        return ResponseEntity.ok(aggregatedData);
    }



}
