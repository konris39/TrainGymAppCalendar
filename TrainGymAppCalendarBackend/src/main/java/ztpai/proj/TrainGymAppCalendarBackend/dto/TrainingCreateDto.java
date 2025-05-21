package ztpai.proj.TrainGymAppCalendarBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class TrainingCreateDto {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private LocalDate trainingDate;

    private boolean askTrainer = false;

    public boolean isAskTrainer() {
        return askTrainer;
    }

    public void setAskTrainer(boolean askTrainer) {
        this.askTrainer = askTrainer;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }
}
