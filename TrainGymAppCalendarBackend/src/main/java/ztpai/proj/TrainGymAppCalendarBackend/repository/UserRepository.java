package ztpai.proj.TrainGymAppCalendarBackend.repository;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import ztpai.proj.TrainGymAppCalendarBackend.models.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserById(Integer id);

    Optional<User> findByMail(String mail);
}