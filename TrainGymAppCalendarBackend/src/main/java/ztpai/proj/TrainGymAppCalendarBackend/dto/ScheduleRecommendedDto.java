package ztpai.proj.TrainGymAppCalendarBackend.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ScheduleRecommendedDto {
    @NotNull
    private LocalDate trainingDate;

    public LocalDate getTrainingDate() {
        return trainingDate;
    }
    public void setTrainingDate(LocalDate trainingDate) {
        this.trainingDate = trainingDate;
    }
}
