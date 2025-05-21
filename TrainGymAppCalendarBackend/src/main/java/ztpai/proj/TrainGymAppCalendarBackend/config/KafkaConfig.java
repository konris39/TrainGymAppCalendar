package ztpai.proj.TrainGymAppCalendarBackend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic trainerRequestsTopic() {
        return new NewTopic("trainer.requests", 1, (short) 1);
    }
}
