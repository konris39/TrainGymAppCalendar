package ztpai.proj.TrainGymAppCalendarBackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserUpdateDto {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String mail;

    private Boolean trainer;
    private Boolean admin;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public Boolean getTrainer() { return trainer; }
    public void setTrainer(Boolean trainer) { this.trainer = trainer; }
    public Boolean getAdmin() { return admin; }
    public void setAdmin(Boolean admin) { this.admin = admin; }
}
