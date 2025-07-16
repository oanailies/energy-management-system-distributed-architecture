package Receiver.listener;

import Receiver.entities.Device;
import Receiver.services.DeviceService;
import org.springframework.stereotype.Service;

@Service
public class DeviceSyncListener {

    private final DeviceService deviceService;

    public DeviceSyncListener(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    public void handleSyncMessage(String message) {
        try {
            System.out.println("-------------------------------------------------");
            System.out.println("Received message from Sync Queue:");
            System.out.println(message);
            System.out.println("-------------------------------------------------");

            String[] parts = message.split("\\+");
            if (parts.length < 6) {
                throw new IllegalArgumentException("Invalid message format: " + message);
            }

            String action = parts[0];
            Long id = Long.parseLong(parts[1]);
            String name = parts[2];
            String description = parts[3];
            String address = parts[4];
            Double maxHourlyConsumption = Double.parseDouble(parts[5]);

            if ("INSERT".equalsIgnoreCase(action)) {
                Device device = new Device();
                device.setId(id);
                device.setName(name);
                device.setDescription(description);
                device.setAddress(address);
                device.setMaxHourlyConsumption(maxHourlyConsumption);
                deviceService.saveDevice(device);
                System.out.println("Device saved to database:");
                System.out.println(device);

            } else if ("DELETE".equalsIgnoreCase(action)) {
                deviceService.deleteDeviceById(id);
                System.out.println("deleted from database with ID: " + id);
            }

            System.out.println("-------------------------------------------------");

        } catch (Exception e) {
            System.err.println("Error processing Sync message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
