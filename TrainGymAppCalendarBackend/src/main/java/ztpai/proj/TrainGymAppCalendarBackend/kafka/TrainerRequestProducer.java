package ztpai.proj.TrainGymAppCalendarBackend.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TrainerRequestProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TrainerRequestProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRequest(String message) {
        kafkaTemplate.send("trainer.requests", message);
    }
}
