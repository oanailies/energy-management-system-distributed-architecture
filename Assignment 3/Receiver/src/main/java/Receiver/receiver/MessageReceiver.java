package Receiver.receiver;

import Receiver.entities.Device;
import Receiver.entities.DeviceMeasurement;
import Receiver.entities.HourlyEnergyConsumption;
import Receiver.repositories.DeviceMeasurementRepository;
import Receiver.repositories.DeviceRepository;
import Receiver.repositories.HourlyEnergyConsumptionRepository;
import Receiver.services.WebSocketNotificationService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class MessageReceiver {

    private final DeviceMeasurementRepository measurementRepository;
    private final HourlyEnergyConsumptionRepository consumptionRepository;
    private final DeviceRepository deviceRepository;
    private final WebSocketNotificationService webSocketNotificationService;

    public MessageReceiver(DeviceMeasurementRepository measurementRepository,
                           HourlyEnergyConsumptionRepository consumptionRepository,
                           DeviceRepository deviceRepository,
                           WebSocketNotificationService notificationService) {
        this.measurementRepository = measurementRepository;
        this.consumptionRepository = consumptionRepository;
        this.deviceRepository = deviceRepository;
        this.webSocketNotificationService = notificationService;
    }

    public void receiveMessage(byte[] messageBody) {
        try {
            String message = new String(messageBody);
            System.out.println("Received message: " + message);
            if (message.startsWith("\"") && message.endsWith("\"")) {
                message = message.substring(1, message.length() - 1);
            }
            message = message.replace("\\\"", "\"");

            JSONObject jsonObject = new JSONObject(message);
            DeviceMeasurement measurement = new DeviceMeasurement();
            measurement.setMeasurementValue(jsonObject.optDouble("measurement_value", Double.NaN));
            measurement.setDeviceId(jsonObject.optLong("device_id", -1));
            measurement.setTimestamp(jsonObject.optLong("timestamp", -1));
            if (Double.isNaN(measurement.getMeasurementValue()) || measurement.getDeviceId() == -1 || measurement.getTimestamp() == -1) {
                throw new IllegalArgumentException("Missing or invalid fields in JSON: " + message);
            }

            measurementRepository.save(measurement);
            System.out.println("Message saved: " + measurement);

            calculateHourlyConsumption(measurement.getDeviceId());

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void calculateHourlyConsumption(Long deviceId) {
        System.out.println("Calculating hourly consumption for device ID: " + deviceId);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime oneHourAgo = now.minusHours(1);

        long startTimestamp = oneHourAgo.toInstant().toEpochMilli();
        long endTimestamp = now.toInstant().toEpochMilli();

        List<DeviceMeasurement> measurements = measurementRepository.findByDeviceIdAndTimestampBetween(deviceId, startTimestamp, endTimestamp);

        if (measurements.isEmpty()) {
            String noMeasurementsMessage = "No measurements found for device ID " + deviceId + " in the last hour.";
            System.out.println(noMeasurementsMessage);
            webSocketNotificationService.sendNotification(String.valueOf(deviceId), noMeasurementsMessage);
            return;
        }

        double totalConsumption = measurements.stream()
                .mapToDouble(DeviceMeasurement::getMeasurementValue)
                .sum();

        HourlyEnergyConsumption existingConsumption = consumptionRepository.findByDeviceIdAndTimestamp(deviceId, startTimestamp);

        if (existingConsumption != null) {
            existingConsumption.setTotalConsumption(totalConsumption);
            consumptionRepository.save(existingConsumption);
        } else {
            HourlyEnergyConsumption consumption = new HourlyEnergyConsumption();
            consumption.setDeviceId(deviceId);
            consumption.setTimestamp(startTimestamp);
            consumption.setTotalConsumption(totalConsumption);
            consumptionRepository.save(consumption);
        }

        System.out.println("Hourly consumption saved for device ID " + deviceId + ": " + totalConsumption);

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found with ID: " + deviceId));

        System.out.println("Max hourly consumption for device ID " + deviceId + ": " + device.getMaxHourlyConsumption());

        if (totalConsumption > device.getMaxHourlyConsumption()) {
            String exceededMessage = "LIMIT EXCEEDED: Total hourly consumption (" + totalConsumption +
                    ") exceeds the maximum limit (" + device.getMaxHourlyConsumption() +
                    ") for device ID: " + deviceId;
            System.out.println(exceededMessage);
            webSocketNotificationService.sendNotification(String.valueOf(deviceId), exceededMessage);
        }
    }




}
