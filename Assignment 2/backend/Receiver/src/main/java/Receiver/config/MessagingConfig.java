package Receiver.config;

import Receiver.receiver.MessageReceiver;
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
public class MessagingConfig {

    private static final String MEASUREMENTS_QUEUE_NAME = "device.measurements.queue";


    @Bean
    public Queue measurementsQueue() {
        return new Queue(MEASUREMENTS_QUEUE_NAME, true);
    }


    @Bean
    public SimpleMessageListenerContainer measurementsContainer(ConnectionFactory connectionFactory,
                                                                MessageListenerAdapter measurementsListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(MEASUREMENTS_QUEUE_NAME);
        container.setMessageListener(measurementsListenerAdapter);
        return container;
    }


    @Bean
    public MessageListenerAdapter measurementsListenerAdapter(MessageReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }


}
