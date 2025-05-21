package ztpai.proj.TrainGymAppCalendarBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ztpai.proj.TrainGymAppCalendarBackend.models.UserGroup;

import java.util.List;

public interface GroupRepository extends JpaRepository<UserGroup, Integer> {

    List<UserGroup> findAllByTrainerId(Integer trainerId);
    List<UserGroup> findAllByUserId(Integer userId);
}
