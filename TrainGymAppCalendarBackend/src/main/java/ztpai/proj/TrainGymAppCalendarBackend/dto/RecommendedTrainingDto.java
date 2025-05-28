package ztpai.proj.TrainGymAppCalendarBackend.dto;

import ztpai.proj.TrainGymAppCalendarBackend.models.TrainingType;

public class RecommendedTrainingDto {
    private Integer id;
    private String name;
    private String description;
    private TrainingType type;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public TrainingType getType() {
        return type;
    }
    public void setType(TrainingType type) {
        this.type = type;
    }
}
