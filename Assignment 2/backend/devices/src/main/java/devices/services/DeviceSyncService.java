package devices.services;

import devices.entities.Device;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.stereotype.Service;

@Service
public class DeviceSyncService {

    private final RabbitTemplate rabbitTemplate;
    private final Exchange deviceSyncExchange;

    public DeviceSyncService(RabbitTemplate rabbitTemplate, Exchange deviceSyncExchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.deviceSyncExchange = deviceSyncExchange;
    }


    public void sendSyncMessage(String action, Device device) {
        String message = action + "+" + device.getId() + "+" + device.getName() + "+" +
                device.getDescription() + "+" + device.getAddress() + "+" +
                device.getMaxHourlyConsumption();
        System.out.println("Sending message: " + message);
        rabbitTemplate.convertAndSend(deviceSyncExchange.getName(), "device.sync.management", message);
    }
}
