package devices.controllers;

import devices.entities.UserDevice;
import devices.repositories.UserDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserDeviceController {

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    @GetMapping("/api/userDevices")
    public List<UserDevice> getAllUserDevices() {
        return userDeviceRepository.findAll();
    }
}
