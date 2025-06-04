package ztpai.proj.TrainGymAppCalendarBackend.dto;

import jakarta.validation.constraints.NotBlank;

public class UserAdminUpdateDto {
    @NotBlank
    private String name;

    private Boolean trainer;
    private Boolean admin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getTrainer() {
        return trainer;
    }

    public void setTrainer(Boolean trainer) {
        this.trainer = trainer;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
