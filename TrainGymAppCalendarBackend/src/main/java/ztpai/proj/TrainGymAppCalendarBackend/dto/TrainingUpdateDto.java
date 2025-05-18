package ztpai.proj.TrainGymAppCalendarBackend.dto;

import java.time.LocalDate;

public class TrainingUpdateDto {
    private String name;
    private String description;
    private LocalDate trainingDate;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getTrainingDate() { return trainingDate; }
    public void setTrainingDate(LocalDate trainingDate) { this.trainingDate = trainingDate; }
}
