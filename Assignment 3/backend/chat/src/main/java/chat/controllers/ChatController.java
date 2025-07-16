package chat.controllers;

import chat.entities.ChatMessage;
import chat.entities.User;
import chat.entities.UserRole;
import chat.services.ChatMessageService;
import chat.services.UserServiceClient;
import chat.services.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://frontend.localhost")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserStatusService userStatusService;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();



    @MessageMapping("/typing")
    public void userTyping(
            @Payload ChatMessage chatMessage,
            @Header("simpSessionAttributes") Map<String, Object> sessionAttributes
    ) {
        System.out.println("Received typing event from senderId: " + chatMessage.getSenderId());

        SecurityContext securityContext = (SecurityContext) sessionAttributes.get("SECURITY_CONTEXT");
        if (securityContext == null) {
            System.out.println("SecurityContext is null. Cannot authenticate WebSocket request.");
            return;
        }

        SecurityContextHolder.setContext(securityContext);

        User sender = userServiceClient.getUserDetails(chatMessage.getSenderId());
        User recipient = userServiceClient.getUserDetails(chatMessage.getRecipientId());

        if (sender == null) {
            System.out.println("Sender not found! ID: " + chatMessage.getSenderId());
            return;
        }
        if (recipient == null) {
            System.out.println("Recipient not found! ID: " + chatMessage.getRecipientId());
            return;
        }

        messagingTemplate.convertAndSend("/topic/typing", chatMessage);
        System.out.println(sender.getName() + " is typing a message to " + recipient.getName());
    }



    @MessageMapping("/mark-as-read")
    public void markMessagesAsRead(@Payload ChatMessage chatMessage,
                                   @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        SecurityContext securityContext = (SecurityContext) sessionAttributes.get("SECURITY_CONTEXT");
        if (securityContext == null) {
            System.out.println("SecurityContext is null. Cannot authenticate WebSocket request.");
            return;
        }
        SecurityContextHolder.setContext(securityContext);

        User recipient = userServiceClient.getUserDetails(chatMessage.getRecipientId());
        if (recipient == null) {
            System.out.println("Recipient not found! ID: " + chatMessage.getRecipientId());
            return;
        }

        boolean isRecipientActive = userStatusService.isUserOnPage(
                chatMessage.getRecipientId(),
                recipient.getRole() == UserRole.ADMIN ? "admin" : "user"
        );

        if (!isRecipientActive) {
            System.out.println("Messages were NOT marked as read. The recipient is inactive.");
            return;
        }

        List<ChatMessage> unreadMessages = chatMessageService.getUnreadMessages(
                chatMessage.getSenderId(),
                chatMessage.getRecipientId()
        );

        if (unreadMessages.isEmpty()) {
            System.out.println("No unread messages for this user.");
            return;
        }

        List<ChatMessage> updatedMessages = chatMessageService.markMessagesAsRead(
                chatMessage.getSenderId(), chatMessage.getRecipientId()
        );

        List<Long> messageIds = updatedMessages.stream()
                .map(ChatMessage::getId)
                .toList();

        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatMessage.getRecipientId()),
                "/queue/read",
                Map.of("messageIds", messageIds)
        );

        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatMessage.getSenderId()),
                "/queue/read",
                Map.of("messageIds", messageIds)
        );

        System.out.println("Messages marked as read and sent via WebSocket: " + messageIds);
    }



    @MessageMapping("/update-status")
    public void updateUserStatus(@Payload Map<String, Object> statusUpdate, @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        SecurityContext securityContext = (SecurityContext) sessionAttributes.get("SECURITY_CONTEXT");
        if (securityContext == null) return;

        SecurityContextHolder.setContext(securityContext);

        Long userId = Long.valueOf(statusUpdate.get("userId").toString());
        String page = statusUpdate.get("page").toString();

        userStatusService.updateUserStatus(userId, page);
        messagingTemplate.convertAndSend("/topic/user-status", statusUpdate);
    }


    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            @Header("simpSessionAttributes") Map<String, Object> sessionAttributes) {
        System.out.println("Received message event from senderId: " + chatMessage.getSenderId());

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) sessionAttributes.get("AUTH_PRINCIPAL");

        if (authentication == null) {
            System.out.println("Token is missing in SecurityContext, checking attributes...");
            return;
        }

        System.out.println("Authentication found in attributes: " + authentication.getName());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User sender = userServiceClient.getUserDetails(chatMessage.getSenderId());
        User recipient = userServiceClient.getUserDetails(chatMessage.getRecipientId());

        if (sender == null) {
            System.out.println(" Sender not found! ID: " + chatMessage.getSenderId());
            return;
        }
        if (recipient == null) {
            System.out.println("Recipient not found! ID: " + chatMessage.getRecipientId());
            return;
        }


        chatMessage.setSenderRole(sender.getRole());

        ChatMessage savedMessage = chatMessageService.saveMessage(chatMessage);

        messagingTemplate.convertAndSend("/topic/admin-messages", savedMessage);
    }


    @GetMapping("/history/{userId}")
    public List<ChatMessage> getMessagesForUser(@PathVariable Long userId) {
        return chatMessageService.getMessagesForUser(userId);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/conversation/{senderId}/{recipientId}")
    public List<ChatMessage> getConversation(@PathVariable Long senderId,
                                             @PathVariable Long recipientId) {
        return chatMessageService.getConversation(senderId, recipientId);
    }
}
