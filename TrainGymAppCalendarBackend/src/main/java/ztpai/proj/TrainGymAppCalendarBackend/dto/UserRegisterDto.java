package ztpai.proj.TrainGymAppCalendarBackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRegisterDto {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String mail;

    @NotBlank
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
