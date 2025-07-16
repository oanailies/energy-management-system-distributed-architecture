package Receiver.config;

import Receiver.listener.DeviceSyncListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class DeviceSyncProperties {

    private static final String SYNC_QUEUE_NAME = "deviceSyncQueue";

    @Bean
    public Queue syncQueue() {
        return new Queue(SYNC_QUEUE_NAME, true);
    }

    @Bean
    public SimpleMessageListenerContainer syncContainer(ConnectionFactory connectionFactory,
                                                        MessageListenerAdapter syncListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(SYNC_QUEUE_NAME);
        container.setMessageListener(syncListenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter syncListenerAdapter(DeviceSyncListener listener) {
        return new MessageListenerAdapter(listener, "handleSyncMessage");
    }
}
