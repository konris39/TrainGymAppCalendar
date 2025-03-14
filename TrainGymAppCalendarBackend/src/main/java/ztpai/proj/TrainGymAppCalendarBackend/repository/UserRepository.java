package ztpai.proj.TrainGymAppCalendarBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;


public interface UserRepository extends JpaRepository<User, Integer> {

}