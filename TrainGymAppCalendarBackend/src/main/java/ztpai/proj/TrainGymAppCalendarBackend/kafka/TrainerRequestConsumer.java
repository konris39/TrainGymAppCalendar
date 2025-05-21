package ztpai.proj.TrainGymAppCalendarBackend.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TrainerRequestConsumer {
    @KafkaListener(topics = "trainer.requests", groupId = "gym-app-group")
    public void handleTrainerRequest(String message){
        System.out.println("Otrzymano event kafkowy: "+message);
    }
}
