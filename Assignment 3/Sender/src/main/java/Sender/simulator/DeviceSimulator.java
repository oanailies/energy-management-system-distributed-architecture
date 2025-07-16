package Sender.simulator;

import Sender.entity.DeviceMeasurement;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class DeviceSimulator {

    @Value("${rabbitmq.routing.json.key}")
    private String routingKey;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public DeviceSimulator(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void start(long deviceId) {
        String csvPath = "/app/sensor.csv";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvPath));
            Timer timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        String line = reader.readLine();

                        if (line == null) {
                            System.out.println("File processing completed. Terminating simulation.");
                            timer.cancel();
                            reader.close();
                            return;
                        }

                        double measurementValue = Double.parseDouble(line.trim());
                        long timestamp = Instant.now().toEpochMilli();

                        DeviceMeasurement measurement = new DeviceMeasurement(measurementValue, deviceId, timestamp);
                        String jsonMessage = objectMapper.writeValueAsString(measurement);
                        rabbitTemplate.convertAndSend(exchange, routingKey, jsonMessage);
                        System.out.println("Sent: " + jsonMessage);

                    } catch (Exception e) {
                        System.err.println("Simulation error: " + e.getMessage());
                        timer.cancel();
                        try {
                            reader.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }, 0, 6000);

        } catch (Exception e) {
            System.err.println("File read error: " + e.getMessage());
        }
    }
}
