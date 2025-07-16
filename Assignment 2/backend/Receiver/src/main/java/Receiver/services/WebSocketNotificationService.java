package Receiver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String topic, String message) {
        String jsonMessage = String.format("{\"message\": \"%s\"}", message);
        messagingTemplate.convertAndSend("/topic/" + topic, jsonMessage);
    }
}

