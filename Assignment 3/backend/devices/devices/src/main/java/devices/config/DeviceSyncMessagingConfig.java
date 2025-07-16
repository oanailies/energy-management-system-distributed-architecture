package devices.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeviceSyncMessagingConfig {


    @Bean
    public TopicExchange deviceSyncExchange() {
        return ExchangeBuilder.topicExchange("device.sync.exchange").durable(true).build();
    }



    @Bean
    public Queue deviceSyncQueue() {
        return QueueBuilder.durable("deviceSyncQueue").build();
    }


    @Bean
    public Binding deviceSyncBinding(Queue deviceSyncQueue, TopicExchange deviceSyncExchange) {
        return BindingBuilder
                .bind(deviceSyncQueue)
                .to(deviceSyncExchange)
                .with("device.sync.management");
    }
}
