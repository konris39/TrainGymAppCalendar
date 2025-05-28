package ztpai.proj.TrainGymAppCalendarBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ztpai.proj.TrainGymAppCalendarBackend.models.RecommendedTrainings;

public interface RecommendedTrainingsRepository extends JpaRepository<RecommendedTrainings, Integer> {
}