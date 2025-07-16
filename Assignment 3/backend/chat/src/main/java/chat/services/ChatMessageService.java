package chat.services;

import chat.entities.ChatMessage;
import chat.repositories.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public List<ChatMessage> getConversation(Long senderId, Long recipientId) {
        if (senderId == null || recipientId == null) {
            System.out.println("getConversation: senderId or recipientId is null!");
            return List.of();
        }

        List<ChatMessage> conversation = chatMessageRepository.findConversation(senderId, recipientId);
        System.out.println("getConversation: Found " + conversation.size() + " messages between " + senderId + " and " + recipientId);
        return conversation;
    }

    public ChatMessage saveMessage(ChatMessage message) {
        if (message == null || message.getSenderId() == null || message.getRecipientId() == null) {
            System.out.println("saveMessage: Invalid message (null senderId or recipientId)!");
            return null;
        }

        message.setTimestamp(LocalDateTime.now());
        ChatMessage savedMessage = chatMessageRepository.save(message);
        System.out.println("saveMessage: Message saved between " + message.getSenderId() + " and " + message.getRecipientId());
        return savedMessage;
    }

    public List<ChatMessage> getMessagesForUser(Long userId) {
        if (userId == null) {
            System.out.println("getMessagesForUser: userId is null!");
            return List.of();
        }

        List<ChatMessage> messages = chatMessageRepository.findByRecipientId(userId);
        System.out.println("getMessagesForUser: " + messages.size() + " messages found for user " + userId);
        return messages;
    }

    public List<ChatMessage> markMessagesAsRead(Long senderId, Long recipientId) {
        if (senderId == null || recipientId == null) {
            System.out.println("markMessagesAsRead: senderId or recipientId is null!");
            return List.of();
        }

        List<ChatMessage> unreadMessages = chatMessageRepository
                .findBySenderIdAndRecipientIdAndReadStatusFalse(senderId, recipientId);

        if (unreadMessages.isEmpty()) {
            System.out.println("markMessagesAsRead: No unread messages for this user.");
            return List.of();
        }

        unreadMessages.forEach(message -> message.setReadStatus(true));
        chatMessageRepository.saveAll(unreadMessages);

        System.out.println("markMessagesAsRead: " + unreadMessages.size() + " messages marked as read between " + senderId + " and " + recipientId);
        return unreadMessages;
    }


    public List<ChatMessage> getUnreadMessages(Long senderId, Long recipientId) {
        if (senderId == null || recipientId == null) {
            System.out.println("getUnreadMessages: senderId or recipientId is null!");
            return List.of();
        }

        List<ChatMessage> unreadMessages = chatMessageRepository
                .findBySenderIdAndRecipientIdAndReadStatusFalse(senderId, recipientId);

        System.out.println("getUnreadMessages: " + unreadMessages.size() + " unread messages found between " + senderId + " and " + recipientId);
        return unreadMessages;
    }
}
