package Sender.controller;

import Sender.simulator.DeviceSimulator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/producer")
@CrossOrigin(origins = "http://frontend.localhost")
public class SimulatorController {
    private final DeviceSimulator deviceSimulator;


    public SimulatorController(DeviceSimulator deviceSimulator) {
        this.deviceSimulator = deviceSimulator;
    }

    @PostMapping("/start")
    public String startSimulation(@RequestParam long deviceId) {
        try {
            deviceSimulator.start(deviceId);
            return "Simulation initiated for device_id: " + deviceId;
        } catch (Exception e) {
            return "Error during simulation start: " + e.getMessage();
        }
    }
}