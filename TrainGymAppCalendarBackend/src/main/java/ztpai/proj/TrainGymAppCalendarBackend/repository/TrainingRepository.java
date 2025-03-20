package ztpai.proj.TrainGymAppCalendarBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ztpai.proj.TrainGymAppCalendarBackend.models.Training;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;


import java.util.List;
import java.util.Optional;


public interface TrainingRepository extends JpaRepository<Training, Integer> {

    List<Training> user(User user);

    List<Training> findAllByUserId(Integer user_id);
}