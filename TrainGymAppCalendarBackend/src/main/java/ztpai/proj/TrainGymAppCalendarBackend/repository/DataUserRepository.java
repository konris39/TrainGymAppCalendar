package ztpai.proj.TrainGymAppCalendarBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ztpai.proj.TrainGymAppCalendarBackend.models.DataUser;

import java.util.List;
import java.util.Optional;

public interface DataUserRepository extends JpaRepository<DataUser, Integer> {

    List<DataUser> findAllByUserId(Integer user_id);

    Optional<DataUser> findByUserId(Integer user_id);
}
