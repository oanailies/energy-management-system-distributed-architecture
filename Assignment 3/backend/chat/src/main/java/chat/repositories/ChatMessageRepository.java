package chat.repositories;

import chat.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRecipientId(Long recipientId);

    List<ChatMessage> findBySenderId(Long senderId);
    
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE (m.senderId = :user1 AND m.recipientId = :user2) " +
            "   OR (m.senderId = :user2 AND m.recipientId = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findConversation(@Param("user1") Long user1, @Param("user2") Long user2);

    List<ChatMessage> findBySenderIdAndRecipientId(Long senderId, Long recipientId);

    List<ChatMessage> findBySenderIdAndRecipientIdAndReadStatusFalse(Long senderId, Long recipientId);

}
